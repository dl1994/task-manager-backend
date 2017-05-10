package at.doml.taskmanager.controllers;

import at.doml.taskmanager.exceptions.UserNotFoundException;
import at.doml.taskmanager.models.db.User;
import at.doml.taskmanager.models.req.UserReq;
import at.doml.taskmanager.models.resp.UserResp;
import at.doml.taskmanager.services.UserService;

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
