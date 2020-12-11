package CAFServer.logic.game.phase;

import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.CAFWebSocketMessageOperation;
import CAFServer.logic.game.gamePhase.Round;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;


public class WinnerPhase extends Phase {

    protected WinnerPhase(Round round, Game game) {
        super(round, game);
        this.setOperation(CAFWebSocketMessageOperation.WINNER_PHASE);

        this.startDeadlineTimer(10);
    }

    @Override
    protected void deadlinePassed() {
        goNext();
    }

    @Override
    protected void goNext() {
        this.stopTimer();

        if(game.getPlayers().stream().anyMatch(i->i.getScore() == game.getSettings().getSCORE_TO_WIN())){
            round.setPhase(new EndPhase(round, game));
            new CAFServerWebSocketMessageHandler().informPhaseContent(game);
            new CAFServerWebSocketMessageHandler().informPhase(game);
        }else{
            game.nextRound();
        }
    }
}
