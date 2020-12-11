package CAFServer.logic.game.phase;

import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.CAFWebSocketMessageOperation;
import CAFServer.logic.game.gamePhase.Round;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;


public class DealPhase extends Phase {
    public DealPhase(Round round, Game game) {
        super(round, game);

        dealCards();
        setCardTzar();
        setBlackCard();

        this.setOperation(CAFWebSocketMessageOperation.DEAL_PHASE);

        this.startDeadlineTimer(5);
    }

    @Override
    protected void goNext() {
        this.stopTimer();
        round.setPhase(new ReadPhase(round, game));
        new CAFServerWebSocketMessageHandler().informPhaseContent(game);
        new CAFServerWebSocketMessageHandler().informPhase(game);

    }

    private void setBlackCard(){
        round.setBlackCard(game.getNextBlackCard());
    }

    private void dealCards(){
        for (Player player: game.getPlayers()) {
            int amount = player.getCards().size();
            int amountToDeal = game.getSettings().getMIN_AMOUNT_OF_CARDS()-amount;
            player.dealCards(game.getNewWhiteCards(amountToDeal));
        }
    }
    private void setCardTzar(){
        round.setCardTzar(game.getPlayers().get(round.getRoundNumber()%game.getPlayers().size()));
    }

    @Override
    protected void deadlinePassed(){
        goNext();
    }
}
