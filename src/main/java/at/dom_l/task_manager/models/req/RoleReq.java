package at.dom_l.task_manager.models.req;

import at.dom_l.task_manager.models.db.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleReq {
    
    private User.Role role;
}
