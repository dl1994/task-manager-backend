package at.dom_l.task_manager.exceptions;

public abstract class AbstractIdNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = -190432318960610513L;
    
    private final Integer id;
    
    protected AbstractIdNotFoundException(String message, Integer id) {
        super(message + id);
        this.id = id;
    }
    
    public Integer getId() {
        return this.id;
    }
}
