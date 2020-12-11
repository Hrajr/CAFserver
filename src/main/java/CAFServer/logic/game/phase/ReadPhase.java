package CAFServer.logic.game.phase;

import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.CAFWebSocketMessageOperation;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.gamePhase.Round;

import java.util.Collections;
import java.util.Comparator;

public class ReadPhase extends Phase {
    public ReadPhase(Round round, Game game){
        super(round,game);
        this.setOperation(CAFWebSocketMessageOperation.READ_PHASE);

        this.startDeadlineTimer(15);
    }

    @Override
    protected void deadlinePassed() {
        goNext();
    }

    @Override
    protected void goNext() {
        this.stopTimer();
        round.setPhase(new PlayPhase(round, game));

        new CAFServerWebSocketMessageHandler().informPhaseContent(game);
        new CAFServerWebSocketMessageHandler().informPhase(game);
    }

}
