package at.dom_l.task_manager.models.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReq {
    
    private String oldPassword;
    private String newPassword;
}
