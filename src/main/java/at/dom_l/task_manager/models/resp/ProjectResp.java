package at.dom_l.task_manager.models.resp;

import at.dom_l.task_manager.models.db.Project;
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
