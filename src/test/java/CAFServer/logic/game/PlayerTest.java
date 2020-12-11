package CAFServer.logic.game;

import CAFServer.logic.cards.WhiteCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    private final int id = 0;
    private final String username = "test";


    @BeforeEach
    void setUp() {
        player = new Player(id, username);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setSession() {
        ///Should these be tested?
    }

    @Test
    void getSession() {
        ///Should these be tested?
    }

    @Test
    void getId() {
        int actual = player.getId();
        int expected = id;
        assertEquals(expected, actual);
    }

    @Test
    void getUsername() {
        String actual = player.getUsername();
        String expected = username;
        assertEquals(expected, actual);
    }

    @Test
    void getScore() {
        int actual = player.getScore();
        int expected = 0;
        assertEquals(expected, actual);
    }

    @Test
    void updateScore() {
        int actual = player.getScore();
        int expected = 0;
        assertEquals(expected, actual);
        player.updateScore();
        actual = player.getScore();
        expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    void getCards() {
        List<WhiteCard> actual = player.getCards();
        List<WhiteCard> expected = new ArrayList<>();
        assertEquals(expected, actual);
    }

    @Test
    void dealCards() {
        List<WhiteCard> actual = player.getCards();
        List<WhiteCard> expected = new ArrayList<>();
        assertEquals(expected, actual);

        List<WhiteCard> cardsDealt = new ArrayList<>();
        cardsDealt.add(new WhiteCard("Card 1"));
        cardsDealt.add(new WhiteCard("Card 2"));
        player.dealCards(cardsDealt);

        actual = player.getCards();
        expected = cardsDealt;
        assertEquals(expected, actual);
    }

    @Test
    void removeCards() {
        List<WhiteCard> actual = player.getCards();
        List<WhiteCard> expected = new ArrayList<>();
        assertEquals(expected, actual);

        List<WhiteCard> cardsDealt = new ArrayList<>();
        cardsDealt.add(new WhiteCard("Card 1"));
        cardsDealt.add(new WhiteCard("Card 2"));
        player.dealCards(cardsDealt);

        actual = player.getCards();
        expected = cardsDealt;
        assertEquals(expected, actual);

        player.removeCards(cardsDealt);
        actual = player.getCards();
        expected = new ArrayList<>();
        assertEquals(expected, actual);
    }
}