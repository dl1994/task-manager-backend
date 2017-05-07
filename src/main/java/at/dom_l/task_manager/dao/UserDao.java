package at.dom_l.task_manager.dao;

import at.dom_l.task_manager.models.db.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserDao extends AbstractDao<User, Integer> {
    
    @Autowired
    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    @Override
    protected Class<User> getModelClass() {
        return User.class;
    }
    
    public Optional<User> getUserByUsername(String username) {
        return this.createQuery("from User where username=:username")
                .setParameter("username", username)
                .uniqueResultOptional();
    }
}
