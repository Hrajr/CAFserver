package CAFServer.logic.cards;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private WhiteCardDeck whiteCardDeck;
    private final int deckSize = 5;

    @BeforeEach
    void setUp() {
        List<WhiteCard> whiteCards = new ArrayList<>(deckSize);
        for (int i = 0; i < deckSize; i++) {
            whiteCards.add(new WhiteCard("Test WhiteCard " + i));
        }
        whiteCardDeck = new WhiteCardDeck(whiteCards);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void dealCard() {
        Card playedWhite = whiteCardDeck.dealCard();

        assertNotNull(playedWhite);
    }

    @Test
    void dealCards() {
        int amount = 5;
        List<WhiteCard> playedWhite = whiteCardDeck.dealCards(amount);

        int expectedWhite = playedWhite.size();

        assertEquals(expectedWhite, amount);
    }

    @Test
    void shuffle() {
        Card initialWhite = whiteCardDeck.deck.get(0);
        whiteCardDeck.shuffle();
        Card afterwardsWhite = whiteCardDeck.deck.get(0);

        assertNotEquals(initialWhite, afterwardsWhite);
    }
}