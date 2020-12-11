package CAFServer.logic.game;

import CAFServer.logic.cards.WhiteCard;
import com.google.gson.annotations.Expose;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

public class Player {
    @Expose
    private final int id;
    @Expose
    private final String username;
    private int score = 0;
    private List<WhiteCard> cards;
    private Session session;

    public Player(int playerId, String username){
        this.id = playerId;
        this.username = username;
        cards = new ArrayList<>();
    }

    public void setSession(Session session){
        this.session = session;
    }

    public Session getSession(){
        return this.session;
    }

    public int getId(){
        return id;
    }
    public String getUsername(){
        return  username;
    }
    public int getScore(){
        return score;
    }
    public void updateScore(){
        score ++;
    }

    public List<WhiteCard> getCards(){
        return cards;
    }
    public void dealCards(List<WhiteCard> cards){
        this.cards.addAll(cards);
    }

    public void removeCards(List<WhiteCard> cards) {
        this.cards.removeAll(cards);
    }
}
