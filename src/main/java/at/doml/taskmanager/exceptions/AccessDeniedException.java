package at.doml.taskmanager.exceptions;

// TODO write handler for this exception
public class AccessDeniedException extends RuntimeException {
    
    private static final long serialVersionUID = 2983081521422048015L;
    
    public AccessDeniedException() {}
    
    public AccessDeniedException(String message) {
        super(message);
    }
}
