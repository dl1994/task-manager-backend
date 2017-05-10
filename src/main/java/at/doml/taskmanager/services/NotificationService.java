package at.doml.taskmanager.services;

import at.doml.taskmanager.dao.NotificationDao;
import at.doml.taskmanager.exceptions.NotificationNotFoundException;
import at.doml.taskmanager.models.db.Notification;
import at.doml.taskmanager.models.db.User;
import at.doml.taskmanager.models.param.PaginationQueryParams;
import at.doml.taskmanager.models.req.NotificationReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {
    
    private final NotificationDao notificationDao;
    
    @Autowired
    public NotificationService(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }
    
    @Transactional(readOnly = true)
    public boolean isOwner(Integer notificationId, User user) {
        return this.notificationDao.getByPrimaryKey(notificationId)
                .map(Notification::getUserId)
                .map(uId -> Objects.equals(uId, user.getId()))
                .orElse(false);
    }
    
    private List<Notification> getNotifications(User user, PaginationQueryParams pagination) {
        return this.notificationDao.getNotifications(user.getId(), pagination);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(User user, PaginationQueryParams pagination) {
        return this.getNotifications(user, pagination);
    }
    
    @Transactional(readOnly = true)
    public Integer getUnseenNotificationsCountForUser(User user) {
        return this.notificationDao.getUnseenCount(user.getId());
    }
    
    private Notification getNotification(Integer notificationId) {
        return this.notificationDao.getByPrimaryKey(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }
    
    @Transactional
    public Integer createNotification(NotificationReq notificationReq) {
        return this.notificationDao.create(
                Notification.builder()
                        .text(notificationReq.getText())
                        .type(notificationReq.getType())
                        .userId(notificationReq.getUserId())
                        .target(notificationReq.getTarget())
                        .status(Notification.Status.UNSEEN)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }
    
    @Transactional
    public void changeStatus(Integer notificationId, Notification.Status status) {
        Notification notification = this.getNotification(notificationId);
        notification.setStatus(status);
        this.notificationDao.update(notification);
    }
    
    @Transactional
    public void deleteNotification(Integer notificationId) {
        this.notificationDao.delete(this.getNotification(notificationId));
    }
}
