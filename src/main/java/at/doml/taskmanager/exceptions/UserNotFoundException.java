package at.doml.taskmanager.exceptions;

// TODO write handler for this exception
public class UserNotFoundException extends AbstractIdNotFoundException {
    
    private static final String MESSAGE = "no user with id: ";
    private static final long serialVersionUID = 944300866983400163L;
    
    public UserNotFoundException(Integer id) {
        super(MESSAGE, id);
    }
}
