package CAFServer.logic.game.phase;

import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.CAFWebSocketMessageOperation;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.gamePhase.Round;

import java.util.Collections;
import java.util.Comparator;

public class EndPhase extends Phase{
    private final Player victor;

    protected EndPhase(Round round, Game game) {
        super(round, game);
        victor = Collections.max(game.getPlayers(), Comparator.comparingInt(Player::getScore));

        this.setOperation(CAFWebSocketMessageOperation.END_PHASE);

        this.startDeadlineTimer(15);
    }

    public Player getVictor(){
        return victor;
    }

    public void newGame(){
        goNext();
    }

    @Override
    protected void goNext() {
        this.stopTimer();

        new CAFServerWebSocketMessageHandler().informEndGame(game);
    }

    @Override
    protected void deadlinePassed(){
        goNext();
    }
}