package CAFServer.logic.game.phase;

import CAFServer.logic.Exceptions.UserNotFoundException;
import CAFServer.logic.cards.WhiteCard;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.gamePhase.Round;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayPhaseTest {
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
        round.setPhase(new PlayPhase(round, game));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void playWhiteCards() {
        PlayPhase pPhase = (PlayPhase)round.getPhase();
        Player playing = game.getPlayers().get(0);
        List<WhiteCard> playedCards = new ArrayList<>();
        for (int i = 0; i < round.getBlackCard().getBlanks(); i++) {
            playedCards.add(new WhiteCard("WhiteCard" + i + "" + playing.getId()));
        }

        try {
            pPhase.playWhiteCards(playing.getId(), playedCards);

            List<WhiteCard> expected = playedCards;
            List<WhiteCard> actual = round.getCardCombinations().get(0);
            assertEquals(expected, actual);

            assertEquals(playing.getId(), round.getCardOwner(expected).getId());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }
}