/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * The MIT License (MIT)                                                           *
 *                                                                                 *
 * Copyright © 2017 Domagoj Latečki                                                *
 *                                                                                 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy    *
 * of this software and associated documentation files (the "Software"), to deal   *
 * in the Software without restriction, including without limitation the rights    *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell       *
 * copies of the Software, and to permit persons to whom the Software is           *
 * furnished to do so, subject to the following conditions:                        *
 *                                                                                 *
 * The above copyright notice and this permission notice shall be included in all  *
 * copies or substantial portions of the Software.                                 *
 *                                                                                 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR      *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,        *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE     *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER          *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,   *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE   *
 * SOFTWARE.                                                                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package at.dom_l.task_manager.services;

import at.dom_l.task_manager.dao.NotificationDao;
import at.dom_l.task_manager.models.db.Notification;
import at.dom_l.task_manager.models.db.User;
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
    
    private List<Notification> getNotifications(User user) {
        return this.notificationDao.getNotifications(user.getId());
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(User user) {
        return this.getNotifications(user);
    }
    
    @Transactional(readOnly = true)
    public Integer getUnseenNotificationsCountForUser(User user) {
        return (int) this.getNotifications(user)
                .stream()
                .filter(n -> n.getStatus() == Notification.Status.UNSEEN)
                .count();
    }
    
    private Notification getNotification(Integer notificationId) {
        return this.notificationDao.getByPrimaryKey(notificationId)
                .orElseThrow(null); // TODO: add exception for this
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
