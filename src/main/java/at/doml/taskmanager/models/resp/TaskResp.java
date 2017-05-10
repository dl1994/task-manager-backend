package at.doml.taskmanager.models.resp;

import at.doml.taskmanager.models.db.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResp {

    private Integer id;
    private Integer priority;
    private String subject;
    private Integer assigneeId;
    private Long createdTimestamp;
    private Long startedTimestamp;
    private Long dueTimestamp;
    private Long finishedTimestamp;
    private Task.Status status;
}
