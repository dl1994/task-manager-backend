package at.dom_l.task_manager.controllers;

import at.dom_l.task_manager.models.db.Comment;
import at.dom_l.task_manager.models.db.Notification;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.req.CommentReq;
import at.dom_l.task_manager.models.req.NotificationReq;
import at.dom_l.task_manager.models.resp.CommentResp;
import at.dom_l.task_manager.services.CommentService;
import at.dom_l.task_manager.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/comments")
public class CommentsController {
    
    private final CommentService commentService;
    private final NotificationService notificationService;
    
    @Autowired
    public CommentsController(CommentService commentService, NotificationService notificationService) {
        this.commentService = commentService;
        this.notificationService = notificationService;
    }
    
    @RequestMapping(value = "/{taskId}", method = GET)
    public List<CommentResp> getCommentsForTask(@PathVariable Integer taskId,
                                                @AuthenticationPrincipal User user) {
        // TODO: check if user is allowed to read comments
        // TODO: add pagination
        return this.commentService.getCommentsForTask(taskId)
                .stream()
                .map(Comment::toResp)
                .collect(Collectors.toList());
    }
    
    @RequestMapping(value = "/{taskId}", method = PUT)
    public CommentResp commentOnTask(@PathVariable Integer taskId,
                                     @RequestBody CommentReq commentReq,
                                     @AuthenticationPrincipal User user) {
        // TODO: check if user is allowed to comment
        Integer commentId = this.commentService.createComment(taskId, user, commentReq);
        
        this.notificationService.createNotification(
                NotificationReq.builder()
                        .target(commentId)
                        .type(Notification.Type.COMMENT)
                        .text(user.getFirstName() + ' ' + user.getLastName() + " commented on task ") // TODO better message
                        .userId(null) // TODO: determine target(s)
                        .build()
        );
        
        return this.commentService.getComment(commentId)
                .map(Comment::toResp)
                .orElseThrow(null); // TODO: figure this out
    }
    
    @RequestMapping(value = "/{commentId}", method = POST)
    public CommentResp editComment(@PathVariable Integer commentId,
                                   @RequestBody CommentReq commentReq,
                                   @AuthenticationPrincipal User user) {
        // TODO: check if user is allowed to edit
        this.commentService.updateComment(commentId, commentReq);
        return this.commentService.getComment(commentId)
                .map(Comment::toResp)
                .orElseThrow(null); // TODO: figure this out
    }
    
    @RequestMapping(value = "/{commentId}", method = DELETE)
    public void deleteComment(@PathVariable Integer commentId,
                              @AuthenticationPrincipal User user) {
        // TODO: check if user is allowd to delete
        this.commentService.deleteComment(commentId);
        // TODO: NYI
    }
}
