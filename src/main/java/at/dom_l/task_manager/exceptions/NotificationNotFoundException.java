package at.dom_l.task_manager.exceptions;

// TODO write handler for this exception
public class NotificationNotFoundException extends AbstractIdNotFoundException {
    
    private static final String MESSAGE = "no notification with id: ";
    private static final long serialVersionUID = -6687044117789588634L;
    
    public NotificationNotFoundException(Integer id) {
        super(MESSAGE, id);
    }
}
