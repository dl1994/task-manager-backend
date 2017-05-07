package at.dom_l.task_manager.models.req;

import at.dom_l.task_manager.models.db.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReq {
    
    private Integer userId;
    private Integer target;
    private String text;
    private Notification.Type type;
}
