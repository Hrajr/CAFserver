package CAFServer.logic.cards;

import java.util.Objects;

public class WhiteCard extends Card{
    public WhiteCard(String text) {
        super(text);
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhiteCard card = (WhiteCard) o;
        return Objects.equals(text, card.text);
    }


}
