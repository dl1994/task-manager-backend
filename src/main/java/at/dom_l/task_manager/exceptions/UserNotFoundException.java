package at.dom_l.task_manager.exceptions;

public class UserNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 3755420465655653360L;
    
    public UserNotFoundException() {}
    
    public UserNotFoundException(String message) {
        super(message);
    }
}
