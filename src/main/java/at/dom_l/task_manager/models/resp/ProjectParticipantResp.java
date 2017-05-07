package at.dom_l.task_manager.models.resp;

import at.dom_l.task_manager.models.db.ProjectParticipant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectParticipantResp {
    
    private Integer projectId;
    private Integer participantId;
    private ProjectParticipant.Role role;
}
