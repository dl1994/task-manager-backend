package at.dom_l.task_manager.controllers;

import at.dom_l.task_manager.exceptions.UserNotFoundException;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.req.UserReq;
import at.dom_l.task_manager.models.resp.UserResp;
import at.dom_l.task_manager.services.UserService;

public abstract class AbstractUserController {
    
    protected final UserService userService;
    
    protected AbstractUserController(UserService userService) {
        this.userService = userService;
    }
    
    protected UserResp changeInfoById(Integer userId, UserReq userReq) {
        this.userService.changeUserInfo(userId, userReq);
        return this.getUserById(userId);
    }
    
    protected UserResp getUserById(Integer userId) {
        return this.userService.loadUserById(userId)
                .map(User::toResp)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
