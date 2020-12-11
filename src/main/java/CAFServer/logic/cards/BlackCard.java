package CAFServer.logic.cards;

import java.util.Objects;

public class BlackCard extends Card{
    private final int blanks;

    public BlackCard(String text, int blanks) {
        super(text);
        this.blanks = blanks;
    }

    public int getBlanks(){
        return blanks;
    }

    @Override
    public String toString() {
        return "["+ blanks + "] " + text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackCard card = (BlackCard) o;
        return Objects.equals(text, card.text) && Objects.equals(blanks, card.blanks);
    }
}
