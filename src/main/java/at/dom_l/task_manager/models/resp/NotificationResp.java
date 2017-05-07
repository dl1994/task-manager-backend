package at.dom_l.task_manager.models.resp;

import at.dom_l.task_manager.models.db.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResp {
    
    private Integer id;
    private Integer userId;
    private String text;
    private Long timestamp;
    private Notification.Status status;
    private Notification.Type type;
    private Integer target;
}
