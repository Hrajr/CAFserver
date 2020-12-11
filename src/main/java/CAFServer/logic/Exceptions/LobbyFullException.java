package CAFServer.logic.Exceptions;

public class LobbyFullException extends Exception{
    public LobbyFullException(int size){super(String.format("Lobby is already full with %s people", size));}
}
