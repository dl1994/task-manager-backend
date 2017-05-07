package at.dom_l.task_manager.models.resp;

import at.dom_l.task_manager.models.db.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResp {

    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private User.Role role;
}
