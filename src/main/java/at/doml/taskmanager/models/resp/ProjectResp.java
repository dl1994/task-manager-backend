package at.doml.taskmanager.models.resp;

import at.doml.taskmanager.models.db.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResp {
    
    private Integer id;
    private Integer ownerId;
    private String name;
    private String description;
    private Project.Status status;
}
