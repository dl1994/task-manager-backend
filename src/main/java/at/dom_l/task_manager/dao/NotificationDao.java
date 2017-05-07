package at.dom_l.task_manager.dao;

import at.dom_l.task_manager.models.db.Notification;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class NotificationDao extends AbstractDao<Notification, Integer> {
    
    @Autowired
    public NotificationDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    @Override
    protected Class<Notification> getModelClass() {
        return Notification.class;
    }
    
    public List<Notification> getNotifications(Integer userId) {
        return this.createQuery("from Notification where userId=:userId")
                .setParameter("userId", userId)
                .getResultList(); // TODO: add pagination, sort by timestamp
    }
}
