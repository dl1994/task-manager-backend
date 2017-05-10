package at.doml.taskmanager.exceptions;

// TODO write handler for this exception
public class CommentNotFoundException extends AbstractIdNotFoundException {
    
    private static final String MESSAGE = "no comment with id: ";
    private static final long serialVersionUID = -5990966643214444187L;
    
    public CommentNotFoundException(Integer id) {
        super(MESSAGE, id);
    }
}
