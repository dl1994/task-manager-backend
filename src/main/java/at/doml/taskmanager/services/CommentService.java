package at.doml.taskmanager.services;

import at.doml.taskmanager.dao.CommentDao;
import at.doml.taskmanager.exceptions.CommentNotFoundException;
import at.doml.taskmanager.models.db.Comment;
import at.doml.taskmanager.models.db.User;
import at.doml.taskmanager.models.param.PaginationQueryParams;
import at.doml.taskmanager.models.req.CommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    
    private final CommentDao commentDao;
    
    @Autowired
    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    @Transactional(readOnly = true)
    public List<Comment> getCommentsForTask(Integer taskId, PaginationQueryParams pagination) {
        return this.commentDao.getComments(taskId, pagination);
    }
    
    @Transactional(readOnly = true)
    public Optional<Comment> getComment(Integer commentId) {
        return this.commentDao.getByPrimaryKey(commentId);
    }
    
    @Transactional
    public Integer createComment(Integer taskId, User poster, CommentReq commentReq) {
        return this.commentDao.create(
                Comment.builder()
                        .taskId(taskId)
                        .posterId(poster.getId())
                        .text(commentReq.getText())
                        .postTimestamp(System.currentTimeMillis())
                        .build()
        );
    }
    
    private Comment getCommentById(Integer commentId) {
        return this.commentDao.getByPrimaryKey(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
    
    @Transactional
    public void updateComment(Integer commentId, CommentReq commentReq) {
        Comment comment = this.getCommentById(commentId);
        comment.setText(comment.getText());
        comment.setLastEditTimestamp(System.currentTimeMillis());
        this.commentDao.update(comment);
    }
    
    @Transactional
    public void deleteComment(Integer commentId) {
        this.commentDao.delete(this.getCommentById(commentId)); // TODO: delete all notifications
    }
}
