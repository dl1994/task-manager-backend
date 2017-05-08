package at.dom_l.task_manager.controllers;

import at.dom_l.task_manager.exceptions.AccessDeniedException;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.req.NewUserReq;
import at.dom_l.task_manager.models.req.PasswordReq;
import at.dom_l.task_manager.models.req.RoleReq;
import at.dom_l.task_manager.models.req.UserReq;
import at.dom_l.task_manager.models.resp.UserResp;
import at.dom_l.task_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserAdministrationController extends AbstractUserController {
    
    @Autowired
    protected UserAdministrationController(UserService userService) {
        super(userService);
    }
    
    @RequestMapping(value = "/{userId}", method = DELETE)
    public void deleteUser(@PathVariable Integer userId,
                           @AuthenticationPrincipal User user) {
        if (notSelf(userId, user)) {
            this.userService.expireSessions(userId);
            this.userService.deleteUser(userId);
        } else {
            throw new AccessDeniedException();
        }
    }
    
    @RequestMapping(value = "/new", method = PUT)
    public UserResp createUser(@RequestBody NewUserReq newUserReq) {
        Integer userId = this.userService.createUser(newUserReq);
        return this.getUserById(userId);
    }
    
    @RequestMapping(value = "/{userId}/change-info", method = POST)
    public UserResp changeInfo(@PathVariable Integer userId,
                               @RequestBody UserReq userReq) {
        return this.changeInfoById(userId, userReq);
    }
    
    @RequestMapping(value = "/{userId}/change-password", method = POST)
    public void changePassword(@PathVariable Integer userId,
                               @RequestBody PasswordReq passwordReq,
                               @AuthenticationPrincipal User user) {
        if (notSelf(userId, user)) {
            this.userService.expireSessions(userId);
            this.userService.changePassword(userId, passwordReq.getNewPassword());
        } else {
            throw new AccessDeniedException();
        }
    }
    
    @RequestMapping(value = "/{userId}/change-role", method = POST)
    public UserResp changeRole(@PathVariable Integer userId,
                               @RequestBody RoleReq roleReq,
                               @AuthenticationPrincipal User user) {
        if (notSelf(userId, user)) {
            this.userService.expireSessions(userId);
            this.userService.changeRole(userId, roleReq.getRole());
            return this.getUserById(userId);
        } else {
            throw new AccessDeniedException();
        }
    }
    
    @RequestMapping(value = "/{userId}/expire-sessions", method = GET)
    public void expireSessions(@PathVariable Integer userId,
                               @AuthenticationPrincipal User user) {
        if (notSelf(userId, user)) {
            this.userService.expireSessions(userId);
        } else {
            throw new AccessDeniedException();
        }
    }
    
    private static boolean notSelf(Integer userId, User user) {
        return !Objects.equals(userId, user.getId());
    }
}
