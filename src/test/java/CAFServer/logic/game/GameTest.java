package CAFServer.logic.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private List<Player> players;
    private Game game;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Player(0, "t1"));
        players.add(new Player(1, "t2"));
        players.add(new Player(2, "t3"));
        players.add(new Player(3, "t4"));
        game = new Game(players);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void isOwner() {
        boolean actual = game.isOwner(players.get(0));
        assertTrue(actual);
    }

    @Test
    void getOwner() {
        Player expected = players.get(0);
        Player actual = game.getOwner();
        assertEquals(expected, actual);
    }

    @Test
    void setOwner() {
        game.setOwner(players.get(1));

        Player expected = players.get(1);
        Player actual = game.getOwner();
        assertEquals(expected, actual);
    }

    @Test
    void addPlayer() {
        Player toAdd = new Player(10, "toAdd");
        game.addPlayer(toAdd);
        assertTrue(game.getPlayers().contains(toAdd));
    }

    @Test
    void removePlayer() {
        Player toAdd = new Player(10, "toAdd");
        game.addPlayer(toAdd);
        assertTrue(game.getPlayers().contains(toAdd));
        game.removePlayer(toAdd);
        assertFalse(game.getPlayers().contains(toAdd));
    }

    @Test
    void getPlayers() {
        assertEquals(players, game.getPlayers());
    }
}