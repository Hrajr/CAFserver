package CAFServer.logic.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Deck<Card> {
    protected final List<Card> deck;
    protected int currentCard;
    protected final int NUMBER_OF_CARDS;
    protected Random rnd;

    public Deck(List<Card> deck){
        this.deck = deck;
        currentCard = 0;
        NUMBER_OF_CARDS = this.deck.size();
        rnd = new Random();
        shuffle();
    }

    public Card dealCard(){
        if(currentCard < deck.size()){
            return deck.get(currentCard++);
        }
        else{
            shuffle();
            return dealCard();
        }
    }
    public List<Card> dealCards(int amount){
        List<Card> cards = new ArrayList<>();
        for(int card = 0; card < amount; card++){
            cards.add(dealCard());
        }
        return cards;
    }

    protected void shuffle(){
        currentCard = 0;
        for(int first = 0; first < deck.size(); first++){
            int second = rnd.nextInt(NUMBER_OF_CARDS);
            Card temp = deck.get(first);
            deck.set(first, deck.get(second));
            deck.set(second, temp);
        }
    }
}
