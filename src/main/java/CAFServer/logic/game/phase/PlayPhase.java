package CAFServer.logic.game.phase;

import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.CAFWebSocketMessageOperation;
import CAFServer.logic.Exceptions.UserNotFoundException;
import CAFServer.logic.cards.WhiteCard;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.gamePhase.Round;
import org.eclipse.jgit.transport.CredentialItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayPhase extends Phase{
    protected PlayPhase(Round round, Game game) {
        super(round, game);
        this.setOperation(CAFWebSocketMessageOperation.PLAY_PHASE);

        this.startDeadlineTimer(10);
    }

    @Override
    protected void goNext() {
        this.stopTimer();
        round.setPhase(new DecidePhase(round, game));

        new CAFServerWebSocketMessageHandler().informPhaseContent(game);
        new CAFServerWebSocketMessageHandler().informPhase(game);
    }

    public void playRandomWhiteCards(int playerId, List<WhiteCard> cards) throws UserNotFoundException{
        List<WhiteCard> randomCards = new ArrayList<>();
        Random random = new Random();
        randomCards.add(cards.get(random.nextInt(cards.size())));
        round.playCards(playerId,randomCards);
    }

    public void playWhiteCards(int playerId, List<WhiteCard> cards) throws UserNotFoundException {
        round.playCards(playerId, cards);

        if(canGoNext()) goNext();
    }
    private boolean canGoNext(){
        return round.getPlayedCards().size() == ((game.getPlayers().size()-1) * round.getBlackCard().getBlanks());
    }

    @Override
    protected void deadlinePassed(){
        timer.stop();
        List<Player> playerWithoutPlayedCards = new ArrayList<>();

        if(round.getPlayedCardsPlayer() != null){
            //Check who didn't play a card yet
            for(Player player: game.getPlayers()){
                if(!round.getPlayedCardsPlayer().contains(player.getId())){
                    if(player != round.getCardTzar()){
                        playerWithoutPlayedCards.add(player);
                    }
                }
            }
        }

        if(playerWithoutPlayedCards.size() != 0){
            //Play random cards for players that didn't chose yet
            for(Player player: playerWithoutPlayedCards){
                try{
                    playRandomWhiteCards(player.getId(),player.getCards());
                }catch (UserNotFoundException ex){

                }

            }

            CAFServerWebSocketMessageHandler webSocketMessageHandler = new CAFServerWebSocketMessageHandler();{
                webSocketMessageHandler.informPlayersWithoutPlayedCards(playerWithoutPlayedCards);
            }
        }

        //Go Next round
        goNext();
    }
}
