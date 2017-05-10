package at.doml.taskmanager.models.resp;

import at.doml.taskmanager.models.db.ProjectParticipant;
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
