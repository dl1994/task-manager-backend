package at.dom_l.task_manager.models.db;

import at.dom_l.task_manager.models.resp.ProjectResp;
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
import javax.persistence.Table;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;
    @Column(length = MAX_DESCRIPTION_LENGTH)
    private String description;
    @Column(nullable = false)
    private Integer ownerId; // TODO: add constraint
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    
    public enum Status {
        ACTIVE, ARCHIVED
    }
    
    public ProjectResp toResp() {
        return ProjectResp.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .ownerId(this.ownerId)
                .status(this.status)
                .build();
    }
}
