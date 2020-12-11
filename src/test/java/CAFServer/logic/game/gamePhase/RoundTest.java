package CAFServer.logic.game.gamePhase;

import CAFServer.logic.Exceptions.UserNotFoundException;
import CAFServer.logic.cards.WhiteCard;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.phase.PlayPhase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {
    private List<Player> players;
    private Game game;
    private Round round;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Player(0, "t1"));
        players.add(new Player(1, "t2"));
        players.add(new Player(2, "t3"));
        players.add(new Player(3, "t4"));
        game = new Game(players);
        game.startNewGame();
        round = game.getCurrentRound();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void playCards() {
        //PlayPhase pPhase = (PlayPhase)round.getPhase();
        //List<WhiteCard> playedCards = new ArrayList<>();
        //for (int i = 0; i < round.getBlackCard().getBlanks(); i++) {
        //    playedCards.add(new WhiteCard("WhiteCard" + i));
        //}
        //try {
        //    pPhase.playWhiteCards(players.get(0).getId(), playedCards);
        //} catch (UserNotFoundException e) {
        //    e.printStackTrace();
        //}
//
        //assertTrue(round.getPlayedCards().contains(playedCards));
    }

    @Test
    void setBlackCard() {
    }

    @Test
    void setCardTzar() {
    }

    @Test
    void getCardTzar() {
    }

    @Test
    void setPhase() {
    }

    @Test
    void getRoundNumber() {
    }

    @Test
    void getPlayedCards() {
    }

    @Test
    void getCardOwner() {
    }

    @Test
    void getCardCombinations() {
    }

    @Test
    void getBlackCard() {
    }

    @Test
    void getPhase() {
    }
}