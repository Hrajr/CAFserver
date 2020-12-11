package CAFServer.logic.game.phase;

import CAFServer.CAFServerWebSocketMessageHandler;
import CAFServer.CAFWebSocketMessageOperation;
import CAFServer.logic.Exceptions.UserNotFoundException;
import CAFServer.logic.cards.WhiteCard;
import CAFServer.logic.game.Game;
import CAFServer.logic.game.Player;
import CAFServer.logic.game.gamePhase.Round;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DecidePhase extends Phase{
    protected DecidePhase(Round round, Game game) {
        super(round, game);
        this.setOperation(CAFWebSocketMessageOperation.DECIDE_PHASE);

        this.startDeadlineTimer(15);
    }

    public Player decideWinner(List<WhiteCard> cardsThatWon) throws UserNotFoundException {
        Player winner = round.getCardOwner(cardsThatWon);
        winner.updateScore();
        round.addWinningCards(winner,cardsThatWon);
        goNext();
        return winner;
    }

    //Added amount if we chose to get more card options
    public void decideRandomWinner(int amount) throws  UserNotFoundException{
        Random random = new Random();
        Set<Integer> keysSet = round.getPlayedCardsMap().keySet();
        List<Integer> keysSetInt = new ArrayList<>(keysSet);
        int randomWinnerId = keysSetInt.get(random.nextInt(round.getPlayedCardsMap().keySet().size()));
        Player winner;
        for(Player player: game.getPlayers()){
            if (player.getId() == randomWinnerId){
                player.updateScore();
                List<WhiteCard> winningCards = round.getPlayedCardsMap().get(randomWinnerId);
                round.addWinningCards(player,winningCards);
            }
        }
    }

    @Override
    protected void goNext() {
        this.stopTimer();
        if(round.getWinningsCards().size() == 0){
            try{
                decideRandomWinner(1);
            }
            catch (UserNotFoundException ex){

            }
        }
        round.setPhase(new WinnerPhase(round,game));
        new CAFServerWebSocketMessageHandler().informPhaseContent(game);
        new CAFServerWebSocketMessageHandler().informPhase(game);
    }

    @Override
    protected void deadlinePassed(){
        goNext();
    }
}
