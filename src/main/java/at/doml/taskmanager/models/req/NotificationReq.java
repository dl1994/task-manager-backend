package at.doml.taskmanager.models.req;

import at.doml.taskmanager.models.db.Notification;
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
