package at.dom_l.task_manager.models.db;

import at.dom_l.task_manager.models.resp.ProjectParticipantResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectParticipant {
    
    @Data
    @Builder
    @Embeddable
    public static class PrimaryKey implements Serializable {
        private static final long serialVersionUID = 7325728769061397712L;
        private Integer projectId; // TODO: add constraint
        private Integer participantId; // TODO: add constraint
    }
    
    @EmbeddedId
    private PrimaryKey primaryKey;
    @Enumerated(EnumType.ORDINAL)
    private Role role;
    
    public enum Role {
        USER, ADMIN, OWNER
    }
    
    public ProjectParticipantResp toResp() {
        return ProjectParticipantResp.builder()
                .projectId(this.primaryKey.getProjectId())
                .participantId(this.primaryKey.getParticipantId())
                .role(this.role)
                .build();
    }
}
