package at.dom_l.task_manager.controllers;

import at.dom_l.task_manager.exceptions.AccessDeniedException;
import at.dom_l.task_manager.models.db.Notification;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.param.PaginationQueryParams;
import at.dom_l.task_manager.models.resp.NotificationResp;
import at.dom_l.task_manager.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {
    
    private final NotificationService notificationService;
    
    @Autowired
    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @RequestMapping(method = GET)
    public List<NotificationResp> getNotifications(@AuthenticationPrincipal User user,
                                                   @ModelAttribute PaginationQueryParams pagination) {
        return this.notificationService.getNotificationsForUser(user, pagination)
                .stream()
                .map(Notification::toResp)
                .collect(Collectors.toList());
    }
    
    @RequestMapping(value = "/unseen-count", method = GET)
    public Integer getUnseenNotificationsCount(@AuthenticationPrincipal User user) {
        return this.notificationService.getUnseenNotificationsCountForUser(user);
    }
    
    private void doIfOwned(User user, Integer notificationId, Consumer<Integer> action) {
        if (this.notificationService.isOwner(notificationId, user)) {
            action.accept(notificationId);
        } else {
            throw new AccessDeniedException();
        }
    }
    
    private void setStatusIfOwned(User user, Integer notificationId, Notification.Status status) {
        this.doIfOwned(user, notificationId, (nId) -> this.notificationService.changeStatus(nId, status));
    }
    
    @RequestMapping(value = "/mark-seen/{notificationId}", method = POST)
    public void markAsSeen(@AuthenticationPrincipal User user,
                           @PathVariable Integer notificationId) {
        // TODO make this bulk-update
        this.setStatusIfOwned(user, notificationId, Notification.Status.SEEN);
    }
    
    @RequestMapping(value = "/mark-clicked/{notificationId}", method = POST)
    public void markAsClicked(@AuthenticationPrincipal User user,
                              @PathVariable Integer notificationId) {
        this.setStatusIfOwned(user, notificationId, Notification.Status.CLICKED);
    }
    
    @RequestMapping(value = "/{notificationId}", method = DELETE)
    public void deleteNotification(@AuthenticationPrincipal User user,
                                   @PathVariable Integer notificationId) {
        this.doIfOwned(user, notificationId, this.notificationService::deleteNotification);
    }
}
