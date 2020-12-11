package CAFServer.logic.game;

import CAFServer.logic.cards.BlackCard;
import CAFServer.logic.cards.WhiteCard;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Settings {


    public Settings() {

        Gson gson = new Gson();
        //DEFAULT
        SCORE_TO_WIN = 7;

        WHITE_CARDS = new ArrayList<>();
        try {
            InputStream input = new URL("http://localhost:8080/api/v1/whitecard").openStream();
            WHITE_CARDS.addAll(gson.fromJson(new InputStreamReader(input, "UTF-8"), new ArrayList<WhiteCard>(){}.getClass()));
        } catch (Exception e){

        };
        WHITE_CARDS.add(new WhiteCard("Gefontyst"));
        WHITE_CARDS.add(new WhiteCard("Proftaak"));

        BLACK_CARDS = new ArrayList<>();
        BLACK_CARDS.add(new BlackCard("Wanneer het koffie-automaat het niet doet ____", 1));

        MIN_AMOUNT_OF_CARDS = 10;
        LOBBY_SIZE = 6;
        MIN_LOBBY_SIZE = 3;
    }

    public Settings(int score_to_win, int min_amount_of_cards, List<WhiteCard> white_cards, List<BlackCard> black_cards, int lobby_size){
        SCORE_TO_WIN = score_to_win;
        MIN_AMOUNT_OF_CARDS = min_amount_of_cards;
        WHITE_CARDS = white_cards;
        BLACK_CARDS = black_cards;
        LOBBY_SIZE = lobby_size;
    }

    public void updateSettings(int score_to_win, int min_amount_of_cards,int lobby_size){
        SCORE_TO_WIN = score_to_win;
        MIN_AMOUNT_OF_CARDS = min_amount_of_cards;
        LOBBY_SIZE = lobby_size;

        //Could add black/white cards here as well if we have multiple sets/categories of them
    }

    public int getSCORE_TO_WIN() {
        return SCORE_TO_WIN;
    }

    public void setSCORE_TO_WIN(int SCORE_TO_WIN) {
        this.SCORE_TO_WIN = SCORE_TO_WIN;
    }

    public List<WhiteCard> getWHITE_CARDS() {
        return WHITE_CARDS;
    }

    public List<BlackCard> getBLACK_CARDS() {
        return BLACK_CARDS;
    }

    public int getMIN_AMOUNT_OF_CARDS() {
        return MIN_AMOUNT_OF_CARDS;
    }

    public void setMIN_AMOUNT_OF_CARDS(int MIN_AMOUNT_OF_CARDS) {
        this.MIN_AMOUNT_OF_CARDS = MIN_AMOUNT_OF_CARDS;
    }

    public int getLOBBY_SIZE() {
        return LOBBY_SIZE;
    }

    public void setLOBBY_SIZE(int LOBBY_SIZE) {
        if(LOBBY_SIZE < MIN_LOBBY_SIZE ){
            LOBBY_SIZE = MIN_LOBBY_SIZE;
        }
        this.LOBBY_SIZE = LOBBY_SIZE;
    }

    public int getMIN_LOBBY_SIZE() {
        return MIN_LOBBY_SIZE;
    }

    public void setMIN_LOBBY_SIZE(int MIN_LOBBY_SIZE) {
        if(MIN_LOBBY_SIZE < 3){
            MIN_LOBBY_SIZE = 3;
        }
        this.MIN_LOBBY_SIZE = MIN_LOBBY_SIZE;
    }

    @Expose
    @SerializedName(value = "scoreToWin")
    private int SCORE_TO_WIN;
    private final List<WhiteCard> WHITE_CARDS;
    private final List<BlackCard> BLACK_CARDS;
    @Expose
    @SerializedName(value = "cardOptionCount")
    private int MIN_AMOUNT_OF_CARDS;
    @Expose
    @SerializedName(value = "maxPlayerCount")
    private int LOBBY_SIZE;
    @Expose
    @SerializedName(value = "minPlayerCount")
    private int MIN_LOBBY_SIZE;
}
