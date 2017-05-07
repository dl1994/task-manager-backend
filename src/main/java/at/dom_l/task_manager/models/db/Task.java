package at.dom_l.task_manager.models.db;

import at.dom_l.task_manager.models.resp.TaskResp;
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
        @Index(columnList = "assigneeId,projectId"),
        @Index(columnList = "projectId"),
        @Index(columnList = "createdTimestamp")
        // TODO figure out if any more indexes are needed
})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private static final int MAX_SUBJECT_LENGTH = 50;

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Integer priority;
    @Column(nullable = false, length = MAX_SUBJECT_LENGTH)
    private String subject;
    @Column
    private Integer assigneeId; // TODO: add constraint
    @Column(nullable = false)
    private Integer projectId; // TODO: add constraint
    @Column(nullable = false)
    private Long createdTimestamp;
    @Column
    private Long startedTimestamp;
    @Column
    private Long dueTimestamp;
    @Column
    private Long finishedTimestamp;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    
    public enum Status {
        NEW, IN_PROGRESS, DONE
    }
    
    public TaskResp toResp() {
        return TaskResp.builder()
                .id(this.id)
                .priority(this.priority)
                .subject(this.subject)
                .assigneeId(this.assigneeId)
                .createdTimestamp(this.createdTimestamp)
                .startedTimestamp(this.startedTimestamp)
                .dueTimestamp(this.dueTimestamp)
                .finishedTimestamp(this.finishedTimestamp)
                .status(this.status)
                .build();
    }
}
