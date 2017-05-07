package at.dom_l.task_manager.controllers;

import at.dom_l.task_manager.exceptions.AccessDeniedException;
import at.dom_l.task_manager.exceptions.CommentNotFoundException;
import at.dom_l.task_manager.models.db.Comment;
import at.dom_l.task_manager.models.db.Notification;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.param.PaginationQueryParams;
import at.dom_l.task_manager.models.req.CommentReq;
import at.dom_l.task_manager.models.req.NotificationReq;
import at.dom_l.task_manager.models.resp.CommentResp;
import at.dom_l.task_manager.services.CommentService;
import at.dom_l.task_manager.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Objects;
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
                                                @ModelAttribute PaginationQueryParams pagination,
                                                @AuthenticationPrincipal User user) {
        if (this.isAssignedToTasksProject(taskId, user.getId()) || isAdmin(user)) {
            return this.commentService.getCommentsForTask(taskId, pagination)
                    .stream()
                    .map(Comment::toResp)
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException();
        }
    }
    
    @RequestMapping(value = "/{taskId}", method = PUT)
    public CommentResp commentOnTask(@PathVariable Integer taskId,
                                     @RequestBody CommentReq commentReq,
                                     @AuthenticationPrincipal User user) {
        if (this.isAssignedToTasksProject(taskId, user.getId()) || isAdmin(user)) {
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
                    .orElseThrow(() -> new CommentNotFoundException(commentId));
        } else {
            throw new AccessDeniedException();
        }
    }
    
    @RequestMapping(value = "/{commentId}", method = POST)
    public CommentResp editComment(@PathVariable Integer commentId,
                                   @RequestBody CommentReq commentReq,
                                   @AuthenticationPrincipal User user) {
        if (this.isCommentOrProjectOwner(commentId, user.getId()) || isAdmin(user)) {
            this.commentService.updateComment(commentId, commentReq);
            return this.commentService.getComment(commentId)
                    .map(Comment::toResp)
                    .orElseThrow(() -> new CommentNotFoundException(commentId));
        } else {
            throw new AccessDeniedException();
        }
    }
    
    @RequestMapping(value = "/{commentId}", method = DELETE)
    public void deleteComment(@PathVariable Integer commentId,
                              @AuthenticationPrincipal User user) {
        if (this.isCommentOrProjectOwner(commentId, user.getId()) || isAdmin(user)) {
            this.commentService.deleteComment(commentId);
            // TODO: NYI
        } else {
            throw new AccessDeniedException();
        }
    }
    
    private boolean isCommentOrProjectOwner(Integer commentId, Integer userId) {
        // TODO add project owner check
        return this.commentService.getComment(commentId)
                .map(comment -> Objects.equals(comment.getPosterId(), userId))
                .orElse(false);
    }
    
    private boolean isAssignedToTasksProject(Integer taskId, Integer userId) {
        // TODO check if user is project owner, or assigned to that project
        return false;
    }
    
    private static boolean isAdmin(User user) {
        return Objects.equals(User.Role.ROLE_ADMIN, user.getRole());
    }
}
