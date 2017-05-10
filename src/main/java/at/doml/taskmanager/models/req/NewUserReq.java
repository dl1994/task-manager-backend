package at.doml.taskmanager.models.req;

import at.doml.taskmanager.models.db.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserReq {
    
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private User.Role role;
}
