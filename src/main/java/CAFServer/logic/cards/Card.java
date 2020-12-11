package CAFServer.logic.cards;



import com.google.gson.annotations.Expose;

import java.util.Random;

public abstract class Card {
    public Card(String text) {
        Random random =  new Random();
        this.id = random.nextInt(9999);
        this.text = text;
    }

    @Expose
    protected final int id;

    @Expose
    protected final String text;

    public String getText(){
        return text;
    }

    @Override
    abstract public String toString();

    @Override
    public abstract boolean equals(Object o);
}
