package at.doml.taskmanager.models.db;

import at.doml.taskmanager.models.resp.NotificationResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Table(indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "target"),
        @Index(columnList = "timestamp"),
})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    private static final int MAX_NOTIFICATION_LENGTH = 500;
    
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Integer userId; // TODO: add constraint
    @Column(nullable = false, length = MAX_NOTIFICATION_LENGTH)
    private String text;
    @Column(nullable = false)
    private Long timestamp;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    @Enumerated(EnumType.ORDINAL)
    private Type type;
    @Column(nullable = false)
    private Integer target; // TODO add constraint
    
    public enum Status {
        UNSEEN, SEEN, CLICKED
    }
    
    public enum Type {
        PROJECT, TASK, COMMENT
    }
    
    public NotificationResp toResp() {
        return NotificationResp.builder()
                .id(this.id)
                .userId(this.userId)
                .text(this.text)
                .timestamp(this.timestamp)
                .status(this.status)
                .type(this.type)
                .target(this.target)
                .build();
    }
}
