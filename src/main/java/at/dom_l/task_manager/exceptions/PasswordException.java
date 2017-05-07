package at.dom_l.task_manager.exceptions;

// TODO write handler for this exception
public class PasswordException extends RuntimeException {
    
    private static final long serialVersionUID = -702316042431305661L;
    
    public PasswordException() {}
    
    public PasswordException(String message) {
        super(message);
    }
}
