package at.doml.taskmanager.dao;

import at.doml.taskmanager.models.db.Notification;
import at.doml.taskmanager.models.param.PaginationQueryParams;
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
    
    public List<Notification> getNotifications(Integer userId, PaginationQueryParams pagination) {
        return this.createQuery("from Notification where userId=:userId order by timestamp desc")
                .setParameter("userId", userId)
                .setFirstResult(pagination.getPage() * pagination.getItemsPerPage())
                .setMaxResults(pagination.getItemsPerPage())
                .getResultList();
    }
    
    public Integer getUnseenCount(Integer userId) {
        return this.createCountQuery("from Notification where userId=:userId and status=:status")
                .setParameter("userId", userId)
                .setParameter("status", Notification.Status.UNSEEN)
                .getSingleResult()
                .intValue();
    }
}
