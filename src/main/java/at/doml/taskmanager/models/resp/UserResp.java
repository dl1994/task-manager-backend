package at.doml.taskmanager.models.resp;

import at.doml.taskmanager.models.db.User;
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
