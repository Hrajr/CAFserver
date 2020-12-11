package CAFServer.logic.game.gamePhase;




import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.logic.Exceptions.UserNotFoundException;
import CAFServer.logic.cards.BlackCard;
import CAFServer.logic.cards.WhiteCard;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.phase.DealPhase;
import CAFServer.logic.game.phase.Phase;
import com.google.gson.annotations.Expose;

import java.util.*;

public class Round {
    private static int ROUND_NUMBER = 0;

    private final Game game;
    @Expose
    private BlackCard blackCard;
    @Expose
    private Player cardTzar;
    private Phase phase;
    private Map<Integer,List<WhiteCard>> playedCards;
    private Map<Player,List<WhiteCard>> winningCards;


    public Round(Game game){
        ROUND_NUMBER ++;
        this.game = game;

        winningCards = new HashMap<>();
        playedCards = new HashMap<>();
        setPhase(new DealPhase(this, game));
    }

    public void playCards(int playerId, List<WhiteCard> cardsPlayed) throws UserNotFoundException {
        Player player = game.getPlayers().stream().filter(i->i.getId() == playerId).findFirst().orElseThrow(
                () -> new UserNotFoundException(playerId)
        );

        playedCards.put(playerId, cardsPlayed);
        player.removeCards(cardsPlayed);
    }


    public void setBlackCard(BlackCard card){
        this.blackCard = card;
    }

    public void setCardTzar(Player player){
        cardTzar = player;
    }
    public Player getCardTzar(){
        return cardTzar;
    }
    public void setPhase(Phase phase){
        this.phase = phase;
    }

    public int getRoundNumber(){
        return ROUND_NUMBER;
    }
    public Set<Integer> getPlayedCardsPlayer(){
        return new HashSet<>(this.playedCards.keySet());
    }
    public List<WhiteCard> getPlayedCards(){
        List<List<WhiteCard>> playedCards = new ArrayList<>(this.playedCards.values());
        List<WhiteCard> toReturn = new ArrayList<>();

        for (List<WhiteCard> cardList: playedCards) {
            toReturn.addAll(cardList);
        }

        return toReturn;
    }

    public Map<Integer,List<WhiteCard>> getPlayedCardsMap(){
        return playedCards;
    }
    public Player getCardOwner(List<WhiteCard> playedCards) throws UserNotFoundException {
        for (Map.Entry<Integer, List<WhiteCard>> entry : this.playedCards.entrySet()){
            if (entry.getValue().equals(playedCards)) {
                return game.getPlayers().stream().filter(i->i.getId() == entry.getKey()).findFirst().orElseThrow(
                        ()-> new UserNotFoundException(entry.getKey())
                );
            }
        }
        return null;
    }
    public List<List<WhiteCard>> getCardCombinations(){
        //TODO: shuffle order to hide keys
        return new ArrayList<>(playedCards.values());
    }
    public BlackCard getBlackCard() {
        return blackCard;
    }

    public Phase getPhase(){
        return phase;
    }

    public Map<Player,List<WhiteCard>> getWinningsCards(){
        return winningCards;
    }

    public void addWinningCards(Player player, List<WhiteCard> cards){
        winningCards.put(player,cards);
    }
}

