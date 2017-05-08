package at.dom_l.task_manager.controllers;

import at.dom_l.task_manager.exceptions.AccessDeniedException;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.req.PasswordReq;
import at.dom_l.task_manager.models.req.UserReq;
import at.dom_l.task_manager.models.resp.UserResp;
import at.dom_l.task_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/users")
public class UsersController extends AbstractUserController {
    
    @Autowired
    public UsersController(UserService userService) {
        super(userService);
    }
    
    @RequestMapping(value = "/me", method = GET)
    public UserResp getMe(@AuthenticationPrincipal User user) {
        return this.getUserById(user.getId());
    }
    
    @RequestMapping(value = "/{userId}", method = GET)
    public UserResp getUser(@PathVariable Integer userId) {
        return this.getUserById(userId);
    }
    
    @RequestMapping(value = "/me/change-info", method = POST)
    public UserResp changeInfo(@RequestBody UserReq userReq,
                               @AuthenticationPrincipal User user) {
        return this.changeInfoById(user.getId(), userReq);
    }
    
    @RequestMapping(value = "/me/change-password", method = POST)
    public void changePassword(@RequestBody PasswordReq passwordReq,
                               @AuthenticationPrincipal User user) {
        if (this.userService.checkPassword(user.getId(), passwordReq.getOldPassword())) {
            this.userService.changePassword(user.getId(), passwordReq.getNewPassword());
        } else {
            throw new AccessDeniedException();
        }
    }
}
