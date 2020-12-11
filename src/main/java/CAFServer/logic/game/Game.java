package CAFServer.logic.game;

import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.logic.Exceptions.LobbyFullException;
import CAFServer.logic.cards.*;
import CAFServer.logic.game.gamePhase.Round;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Game {
    @Expose
    private Player owner;
    @Expose
    private List<Player> players;
    private List<Round> rounds;
    private Round currentRound;
    @Expose
    private Settings settings;
    private WhiteCardDeck whiteCards;
    private BlackCardDeck blackCards;


    public Game(List<Player> firstPlayer){
        this.owner = firstPlayer.get(0);
        this.players = firstPlayer;
        this.settings = new Settings();
    }

    public boolean isOwner(Player player){
        System.out.println(player.getId() + " - " + this.owner.getId());
        if(player.getId() == this.owner.getId()){
            return true;
        }
        return false;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }

    public boolean removePlayer(Player player){
        return this.players.remove(player);
    }

    public List<Player> getPlayers(){
        return players;
    }

    public Settings getSettings() {
        return settings;
    }

    private boolean gameIsOngoing(){
        return players.stream().anyMatch(player -> player.getScore() == settings.getSCORE_TO_WIN());
    }

    public Player getWinner(){
        return Collections.max(players, Comparator.comparingInt(p->p.getScore()));
    }

    public BlackCard getNextBlackCard() {
        return blackCards.dealCard();
    }

    public List<WhiteCard> getNewWhiteCards(int amount) {
        return whiteCards.dealCards(amount);
    }

    public boolean hasEnoughPlayers(){
        if(players.size() >= settings.getMIN_LOBBY_SIZE()){
            return true;
        }
        return false;
    }

    public void startNewGame() {
        this.whiteCards = new WhiteCardDeck(settings.getWHITE_CARDS());
        this.blackCards = new BlackCardDeck(settings.getBLACK_CARDS());
        rounds = new ArrayList<>();

        nextRound();
    }

    public void joinLobby(Player player) throws LobbyFullException {
        if(players.size() >= settings.getLOBBY_SIZE()){
            throw new LobbyFullException(players.size());
        }
        players.add(player);
    }

    public List<WhiteCard> dealCard(int amount) {
        return whiteCards.dealCards(amount);
    }

    public Round getCurrentRound(){
        return currentRound;
    }

    public void nextRound(){
        currentRound = new Round(this);
        rounds.add(currentRound);

        new CAFServerWebSocketMessageHandler().informPhaseContent(this);
        new CAFServerWebSocketMessageHandler().informPhase(this);
        new CAFServerWebSocketMessageHandler().informNewRound(this);
    }
}
