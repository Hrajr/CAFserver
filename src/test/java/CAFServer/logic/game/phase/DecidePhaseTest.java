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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecidePhaseTest {
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
        try {
            finishPlayPhase();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    void finishPlayPhase() throws UserNotFoundException {
        round.setPhase(new PlayPhase(round, game));
        PlayPhase pPhase = (PlayPhase)game.getCurrentRound().getPhase();
        for (Player player:game.getPlayers()) {
            List<WhiteCard> playedCards = new ArrayList<>();
            if(player.equals(round.getCardTzar())) continue;
            for (int i = 0; i < round.getBlackCard().getBlanks(); i++) {
                playedCards.add(new WhiteCard("WhiteCard" + i + "" + player.getId()));
            }
            pPhase.playWhiteCards(player.getId(), playedCards);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void decideWinner() {
        List<Player> players = game.getPlayers().stream().filter(i->!i.equals(round.getCardTzar())).collect(Collectors.toList());
        DecidePhase dPhase = (DecidePhase)round.getPhase();
        Player expected = players.get(0);
        List<WhiteCard> playedCards = new ArrayList<>();
        for (int i = 0; i < round.getBlackCard().getBlanks(); i++) {
            playedCards.add(new WhiteCard("WhiteCard" + i + "" + expected.getId()));
        }
        try {
            Player actual = dPhase.decideWinner(playedCards);
            assertEquals(expected, actual);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }
}