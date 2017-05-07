package at.dom_l.task_manager.dao;

import at.dom_l.task_manager.models.db.Comment;
import at.dom_l.task_manager.models.param.PaginationQueryParams;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CommentDao extends AbstractDao<Comment, Integer> {
    
    @Autowired
    public CommentDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    @Override
    protected Class<Comment> getModelClass() {
        return Comment.class;
    }
    
    public List<Comment> getComments(Integer taskId, PaginationQueryParams pagination) {
        return this.createQuery("from Comment where taskId=:taskId order by postTimestamp asc")
                .setParameter("taskId", taskId)
                .setFirstResult(pagination.getPage() * pagination.getItemsPerPage())
                .setMaxResults(pagination.getItemsPerPage())
                .getResultList();
    }
}
