package CAFServer;


import CAFServer.logic.Exceptions.LobbyFullException;
import CAFServer.logic.Exceptions.UserNotFoundException;
import CAFServer.logic.cards.Card;
import CAFServer.logic.cards.WhiteCard;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.Settings;
import CAFServer.logic.game.gamePhase.Round;
import CAFServer.logic.game.phase.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.websocket.Session;
import java.util.*;


public class CAFServerWebSocketMessageHandler {

//Hashmap to store the games that players can still join with the room code as key
private static final HashMap<Integer, Game> availableGames = new HashMap<>();

////Hashmap to store the games that are already running with the room code as key
//private static final HashMap<Integer, Game> startedGames = new HashMap<>();

//Hashmap to store players and their session
private static final HashMap<Session, Player> players = new HashMap<>();

    Gson gson = new Gson();
    Gson excludeGson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public void handleMessage(CAFWebSocketMessage message, Session session) {
        Player player = null;
        if(players.containsKey(session)){
            player = players.get(session);
        }

        // Operation defined in message
        CAFWebSocketMessageOperation operation;
        operation = message.getOperation();

        // Process message based on operation
        String property = message.getProperty();

        if (null != operation) {
            switch (operation) {
                case REGISTER_PLAYER:
                    Random random = new Random();
                    Player inputPlayer = gson.fromJson(message.getContent(), Player.class);
                    Player registeredPlayer = new Player(random.nextInt(9999),inputPlayer.getUsername());
                    registeredPlayer.setSession(session);
                    players.put(session,registeredPlayer);

                    //Retrieve gameInfo and set it as message
                    message.setOperation(CAFWebSocketMessageOperation.REGISTERED_PLAYER);
                    message.setContent(excludeGson.toJson(registeredPlayer));

                    //Send message back with the result
                    session.getAsyncRemote().sendText(gson.toJson(message));
                    break;

                case GAME_GET_INFO:
                    //Get the code for the game we wish to get info from
                    int roomCodeToGetIntoFrom = Integer.parseInt(message.getProperty());

                    //Retrieve gameInfo and set it as message
                    message.setOperation(CAFWebSocketMessageOperation.GAME_INFO);
                    message.setContent(excludeGson.toJson(availableGames.get(roomCodeToGetIntoFrom)));

                    //Send message back with the result
                    session.getAsyncRemote().sendText(gson.toJson(message));
                    break;

                case START_GAME:
                    //Get the room the player wishes to start and the player
                    int roomCodeToStart = Integer.parseInt(message.getProperty());
                    Game game = availableGames.get(roomCodeToStart);
                    //Start the game if the player is an owner
                    if(game.isOwner(player)){

                        //Check if the game has enough players
                        if(game.hasEnoughPlayers()){
                            game.startNewGame();
                            message.setContent(excludeGson.toJson(game));
                            message.setOperation(CAFWebSocketMessageOperation.GAME_STARTED);

                            for(Player playerInGame: game.getPlayers()){
                                playerInGame.getSession().getAsyncRemote().sendText(gson.toJson(message));

//                                CAFWebSocketMessage dealCards = new CAFWebSocketMessage();
//                                dealCards.setOperation(CAFWebSocketMessageOperation.DEAL_CARDS);
//                                dealCards.setContent(excludeGson.toJson(playerInGame.getCards()));
//                                playerInGame.getSession().getAsyncRemote().sendText(gson.toJson(dealCards));

//                                CAFWebSocketMessage roundInfo = new CAFWebSocketMessage();
//                                roundInfo.setOperation(CAFWebSocketMessageOperation.ROUND_INFO);
//                                roundInfo.setContent(excludeGson.toJson(game.getCurrentRound()));
//                                playerInGame.getSession().getAsyncRemote().sendText(gson.toJson(roundInfo));
                            }
                        }
                        else{
                            message.setContent(String.format("The minimum amount of players to start is %1$d , you got %2$d", availableGames.get(roomCodeToStart).getSettings().getMIN_LOBBY_SIZE(),availableGames.get(roomCodeToStart).getPlayers().size() ));
                            message.setOperation(CAFWebSocketMessageOperation.ERROR);
                            session.getAsyncRemote().sendText(gson.toJson(message));
                        }
                    }
                    //Game not enough players
                    else{
                        message.setContent("You are not the owner of the game room");
                        message.setOperation(CAFWebSocketMessageOperation.ERROR);
                        session.getAsyncRemote().sendText(gson.toJson(message));
                    }
                    break;

                case UPDATE_GAME_SETTINGS:
                    //Get the room the player wishes to update
                    int roomCodeToUpdate = Integer.parseInt(message.getProperty());

                    //Get the new settings the player wishes to implement
                    Settings newSettings = excludeGson.fromJson(message.getContent(), Settings.class);

                    //Update settings of the game if the player is owner
                    if(availableGames.get(roomCodeToUpdate).isOwner(player)){
                        availableGames.get(roomCodeToUpdate).getSettings().updateSettings(newSettings.getSCORE_TO_WIN(),newSettings.getMIN_AMOUNT_OF_CARDS(),newSettings.getLOBBY_SIZE());
                        message.setContent(excludeGson.toJson(availableGames.get(roomCodeToUpdate).getSettings()));
                        message.setOperation(CAFWebSocketMessageOperation.GAME_SETTINGS_UPDATED);

                        //Send everyone in the game a message that a new player joined
                        for(Player playerInGame: availableGames.get(roomCodeToUpdate).getPlayers()){
                            playerInGame.getSession().getAsyncRemote().sendText(gson.toJson(message));
                        }
                    }
                    else{
                        message.setContent("You are not the owner");
                        message.setOperation(CAFWebSocketMessageOperation.ERROR);
                        session.getAsyncRemote().sendText(gson.toJson(message));

                    }
                    break;

                case CREATE_ROOM:
                    //Generate new roomCode
                    int joinCode = generateNewJoinCode();

                    //Create room and add the player to it
                    List<Player> playersForGame = new ArrayList<>();
                    playersForGame.add(player);
                    Game newGame = new Game(playersForGame);

                    //Add room to the available games
                    availableGames.put(joinCode,newGame);

                    //Set message content/operation and send message back to client
                    message.setContent(String.valueOf(joinCode));
                    message.setOperation(CAFWebSocketMessageOperation.CREATED_ROOM);
                    session.getAsyncRemote().sendText(gson.toJson(message));

                    //Set message content/operation and send message back to client
                    message.setContent(excludeGson.toJson(availableGames.get(joinCode)));
                    message.setOperation(CAFWebSocketMessageOperation.GAME_INFO);

                    session.getAsyncRemote().sendText(gson.toJson(message));
                    break;

                case JOIN_ROOM:
                    //Boolean to store if the player joined a room or not | String to store full lobby exception
                    boolean joinedGame = false;
                    String isLobbyFull ="";
                    String isRunning ="";

                    //Get the room the player wishes to join
                    int joinRoomCode = Integer.parseInt(message.getContent());

                    //If there is a game that is not running  with the given join code, add the player to that game
                    for(Integer codeId: availableGames.keySet()){
                        if(codeId == joinRoomCode){
                            if(availableGames.get(joinRoomCode).getCurrentRound() != null ){
                                isRunning = "The game is already running";
                            }
                            else{
                                try{
                                    availableGames.get(joinRoomCode).joinLobby(player);
                                    joinedGame = true;
                                }
                                catch(LobbyFullException ex){
                                    isLobbyFull = ex.getMessage();
                                }
                            }
                        }
                    }
                    //If the player joined a game, sent back the player id and name, otherwise give an error
                    if(joinedGame){
                        message.setOperation(CAFWebSocketMessageOperation.JOINED_ROOM);
                        session.getAsyncRemote().sendText(gson.toJson(message));

                        message.setContent(excludeGson.toJson(availableGames.get(joinRoomCode)));
                        message.setOperation(CAFWebSocketMessageOperation.GAME_INFO);
                        session.getAsyncRemote().sendText(gson.toJson(message));

                        message.setContent(excludeGson.toJson(player));
                        message.setOperation(CAFWebSocketMessageOperation.GAME_PLAYER_JOINED);

                        //Send everyone in the game a message that a new player joined
                        for(Player playerInGame: availableGames.get(joinRoomCode).getPlayers()){
                            if(player != playerInGame)
                                playerInGame.getSession().getAsyncRemote().sendText(gson.toJson(message));
                        }

                    }
                    else{
                        if(isLobbyFull.isEmpty()){
                            message.setContent("There is no room available with the given code");
                        }
                        else if(!isRunning.isEmpty()){
                            message.setContent(isRunning);
                        }
                        else{
                            message.setContent(isLobbyFull);
                        }
                        message.setOperation(CAFWebSocketMessageOperation.ERROR);
                        session.getAsyncRemote().sendText(gson.toJson(message));
                    }
                    break;
                case LEAVE_ROOM:
                    disconnectPlayerFromAllRooms(player);
                    break;
                case CHOOSE_WINNING_CARD:
                    int roomCodeToWinningCard = Integer.parseInt(message.getProperty());
                    Phase currentPhase = availableGames.get(roomCodeToWinningCard).getCurrentRound().getPhase();

                    //Get the new settings the player wishes to implement
                    WhiteCard winningCard = excludeGson.fromJson(message.getContent(), WhiteCard.class);

                    if(currentPhase.getClass() == DecidePhase.class){
                        List<WhiteCard> winningCardList = new ArrayList<>();
                        winningCardList.add(winningCard);
                        try{
                            ((DecidePhase) currentPhase).decideWinner(winningCardList);
                        }
                        catch (UserNotFoundException ex){
                            message.setContent(ex.getMessage());
                            message.setOperation(CAFWebSocketMessageOperation.ERROR);
                        }
                    }
                    break;
                case PLAY_WHITE_CARD:
                    int roomCodeToPlayCard = Integer.parseInt(message.getProperty());
                    Round round = availableGames.get(roomCodeToPlayCard).getCurrentRound();
                    Phase phase = round.getPhase();

                    //Get the new settings the player wishes to implement
                    WhiteCard card = excludeGson.fromJson(message.getContent(), WhiteCard.class);

                    //play card if not card tzar
                    if(round.getCardTzar() != player){
                        List<WhiteCard> cards = new ArrayList<WhiteCard>();
                        cards.add(card);

                        if(phase.getClass() == PlayPhase.class){
                            try{
                                ((PlayPhase) phase).playWhiteCards(player.getId(), cards);
                                message.setContent(excludeGson.toJson(card));
                                message.setOperation(CAFWebSocketMessageOperation.PLAYED_WHITE_CARD);
                            }
                            catch (UserNotFoundException ex){
                                message.setContent(ex.getMessage());
                                message.setOperation(CAFWebSocketMessageOperation.ERROR);
                            }
                        }

                        player.getSession().getAsyncRemote().sendText(gson.toJson(message));
                    }
                    else{
                        message.setContent("You are the Card Tzar");
                        message.setOperation(CAFWebSocketMessageOperation.ERROR);
                        session.getAsyncRemote().sendText(gson.toJson(message));

                    }
                    break;
                default:
                    break;
            }
        }
    }

    public boolean checkForJoinCode(int joinCode){
        //Check if the joinCode is already in available or started games
        if(availableGames.containsKey(joinCode)){
            return false;
        }
//        else if(startedGames.containsKey(joinCode)){
//            return false;
//        }
        //Not in either of the hashmaps -> return true
        return true;
    }

    public int generateNewJoinCode(){
        int joinCode = 0;
        //Defining the max and min number
        int min = 1000;
        int max = 9999;
        boolean newCodeCreated = false;

        //While a new code hasn't been created, make a new one and try to see if doesn't exist.
        while(!newCodeCreated){
            joinCode = (int)(Math.random() * (max - min + 1)+min);
            if(checkForJoinCode(joinCode)){
                newCodeCreated = true;
            }
        }
        return joinCode;
    }

    public void disconnectClient(Session session) {
        Player disConPlayer = players.get(session);
        if(disConPlayer == null)
            return;

        players.remove(session);

        disconnectPlayerFromAllRooms(disConPlayer);
    }

    public void disconnectPlayerFromAllRooms(Player disConPlayer) {
        CAFWebSocketMessage leftInfo = new CAFWebSocketMessage();
        leftInfo.setContent(excludeGson.toJson(disConPlayer));
        leftInfo.setOperation(CAFWebSocketMessageOperation.GAME_PLAYER_LEFT);

        for (Game game: availableGames.values()) {
            boolean left = game.removePlayer(disConPlayer);
            if(left)
                for(Player playerInGame: game.getPlayers())
                    if(disConPlayer != playerInGame)
                        playerInGame.getSession().getAsyncRemote().sendText(gson.toJson(leftInfo));
        }
    }

    public void informPlayersWithoutPlayedCards(List<Player> playersWithoutPlayerCards){

        CAFWebSocketMessage webSocketMessage = new CAFWebSocketMessage();
        webSocketMessage.setOperation(CAFWebSocketMessageOperation.ERROR);
        webSocketMessage.setContent("You didn't chose a card in time");

        for(Player player: playersWithoutPlayerCards){
            for(Player sessionPlayer: players.values()){
                if(sessionPlayer == player){
                    sessionPlayer.getSession().getAsyncRemote().sendText(gson.toJson(webSocketMessage));
                }
            }
        }

    }

    public void informPlayersTimer(List<Player> playersInGame, int time){
        CAFWebSocketMessage message = new CAFWebSocketMessage();
        message.setOperation(CAFWebSocketMessageOperation.TIMER_INFO);
        int timeInSeconds = Math.round(time/1000);
        message.setContent(String.valueOf(timeInSeconds));

        for(Player player: playersInGame){
            for(Player sessionPlayer: players.values()){
                if(sessionPlayer == player){
                    sessionPlayer.getSession().getAsyncRemote().sendText(gson.toJson(message));
                }
            }
        }
    }

    public void informPhase(Game game) {
        CAFWebSocketMessage message = new CAFWebSocketMessage();
        Phase phase = game.getCurrentRound().getPhase();
//        System.out.println(phase);
        message.setOperation(phase.getOperation());
//        System.out.println(phase.getOperation());

        for (Player player : game.getPlayers()) {
            player.getSession().getAsyncRemote().sendText(gson.toJson(message));

//            if (phase instanceof DealPhase) {
//                message.setOperation(CAFWebSocketMessageOperation.DEAL_CARDS);
//                message.setContent(excludeGson.toJson(player.getCards()));
//                player.getSession().getAsyncRemote().sendText(gson.toJson(message));
//            } else if (phase instanceof DecidePhase) {
//                if (player == phase.getRound().getCardTzar()) {
//                    message.setOperation(CAFWebSocketMessageOperation.SUBMITTED_CARDS);
//                    message.setContent(excludeGson.toJson(phase.getRound().getPlayedCards()));
//                    player.getSession().getAsyncRemote().sendText(gson.toJson(message));
//                }
//            } else if (phase instanceof EndPhase) {
//                message.setOperation(CAFWebSocketMessageOperation.GAME_WON);
//                EndPhase endPhase = (EndPhase) phase;
//                message.setContent(excludeGson.toJson(endPhase.getVictor()));
//                player.getSession().getAsyncRemote().sendText(gson.toJson(message));
//            }
        }

    }

    public void informNewRound(Game game){
        CAFWebSocketMessage message = new CAFWebSocketMessage();
        message.setOperation(CAFWebSocketMessageOperation.ROUND_INFO);
        message.setContent(excludeGson.toJson(game.getCurrentRound()));
        for(Player player: game.getPlayers()) {
            player.getSession().getAsyncRemote().sendText(gson.toJson(message));
        }
    }

    public void informEndGame(Game game){
        CAFWebSocketMessage message = new CAFWebSocketMessage();
        message.setOperation(CAFWebSocketMessageOperation.GAME_DONE);
        for(Player player: game.getPlayers()){
            player.getSession().getAsyncRemote().sendText(excludeGson.toJson(message));
        }
        availableGames.remove(game);
    }

    public void informPhaseContent(Game game){
        CAFWebSocketMessage message = new CAFWebSocketMessage();
        Phase phase = game.getCurrentRound().getPhase();

        System.out.println(phase.getOperation());

        for (Player player : game.getPlayers()) {
            System.out.println(excludeGson.toJson(player));
            Session session = player.getSession();

            switch(phase.getOperation()){
                case DEAL_PHASE:
                    message.setOperation(CAFWebSocketMessageOperation.DEAL_CARDS);
                    message.setContent(excludeGson.toJson(player.getCards()));
                    session.getAsyncRemote().sendText(gson.toJson(message));
                    System.out.println("check 1");
                    System.out.println(gson.toJson(message));
                    break;
                case DECIDE_PHASE:
                    if (player == phase.getRound().getCardTzar()) {
                        message.setOperation(CAFWebSocketMessageOperation.SUBMITTED_CARDS);
                        message.setContent(excludeGson.toJson(phase.getRound().getPlayedCards()));
                        session.getAsyncRemote().sendText(gson.toJson(message));
                        System.out.println("check 2");
                    }
                    break;
                case WINNER_PHASE:
                    Map<Player,List<WhiteCard>> winningCards = phase.getRound().getWinningsCards();
                    Map.Entry<Player, List<WhiteCard>> entry = winningCards.entrySet().iterator().next();
                    Player winnerPlayer = entry.getKey();
                    List<WhiteCard> winnerCards = entry.getValue();

                    message.setOperation(CAFWebSocketMessageOperation.WINNER_INFO);
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put( "player", winnerPlayer );
                    data.put( "cards", winnerCards );
                    message.setContent(excludeGson.toJson(data));

                    session.getAsyncRemote().sendText(gson.toJson(message));
                    break;
                case END_PHASE:
                    message.setOperation(CAFWebSocketMessageOperation.GAME_WON_BY);
                    EndPhase endPhase = (EndPhase) phase;
                    message.setContent(excludeGson.toJson(endPhase.getVictor()));
                    session.getAsyncRemote().sendText(gson.toJson(message));
                    System.out.println("check 3");

                    break;
            }

        }
    }

}

