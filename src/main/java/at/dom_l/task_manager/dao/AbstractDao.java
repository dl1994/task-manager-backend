package at.dom_l.task_manager.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.io.Serializable;
import java.util.Optional;

public abstract class AbstractDao<M, PK extends Serializable> {
    
    private final SessionFactory sessionFactory;
    
    protected AbstractDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    protected Session currentSession() {
        return this.sessionFactory.getCurrentSession();
    }
    
    protected Query<M> createQuery(String query) {
        return this.currentSession()
                .createQuery(query, this.getModelClass());
    }
    
    protected abstract Class<M> getModelClass();
    
    public Optional<M> getByPrimaryKey(PK primaryKey) {
        return this.currentSession()
                .byId(this.getModelClass())
                .loadOptional(primaryKey);
    }
    
    @SuppressWarnings("unchecked")
    public PK create(M model) {
        return (PK) this.currentSession()
                .save(model);
    }
    
    public void update(M model) {
        this.currentSession()
                .update(model);
    }
    
    public void delete(M model) {
        this.currentSession()
                .delete(model);
    }
}
