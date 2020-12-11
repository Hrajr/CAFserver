package CAFServer.logic.game.phase;
import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.CAFWebSocketMessage;
import CAFServer.CAFWebSocketMessageOperation;
import CAFServer.logic.game.gamePhase.*;
import CAFServer.logic.game.*;

import javax.swing.Timer;

public abstract class Phase {
    protected final Round round;
    protected final Game game;
    protected Timer timer;
    protected CAFWebSocketMessageOperation operation;

    protected Phase(Round round, Game game) {
        this.round = round;
        this.game = game;
    }

    protected void startDeadlineTimer(Integer deadline){
        this.timer = new Timer(deadline * 1000, e -> {
            this.deadlinePassed();
        });

        //Send new timer to client
        CAFServerWebSocketMessageHandler cafServerWebSocketMessageHandler = new CAFServerWebSocketMessageHandler();
        cafServerWebSocketMessageHandler.informPlayersTimer(this.game.getPlayers(), timer.getDelay());

        this.timer.setRepeats(false);
        this.timer.start();
    }
    protected abstract void deadlinePassed();
    protected abstract void goNext();

    public CAFWebSocketMessageOperation getOperation(){
        return this.operation;
    }

    public void setOperation(CAFWebSocketMessageOperation newOperation){
        this.operation = newOperation;
    }

    public void stopTimer(){
        this.timer.stop();
    }

    public Game getGame(){
        return this.game;
    }

    public Round getRound(){return this.round;}
}
