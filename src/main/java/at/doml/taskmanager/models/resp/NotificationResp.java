package at.doml.taskmanager.models.resp;

import at.doml.taskmanager.models.db.Notification;
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
