package CAFServer.logic.Exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(int id) {super(String.format("User with id %s not found", id));}
}
