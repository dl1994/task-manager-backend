package at.doml.taskmanager.models.db;

import at.doml.taskmanager.models.resp.CommentResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Table(indexes = {
        @Index(columnList = "taskId"),
        @Index(columnList = "posterId"),
        @Index(columnList = "postTimestamp")
})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    
    private static final int MAX_COMMENT_LENGTH = 1_000;
    
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Integer taskId; // TODO: add constraint
    @Column(nullable = false)
    private Integer posterId; // TODO: add constraint
    @Column(nullable = false, length = MAX_COMMENT_LENGTH)
    private String text;
    @Column(nullable = false)
    private Long postTimestamp;
    private Long lastEditTimestamp;
    
    public CommentResp toResp() {
        return CommentResp.builder()
                .id(this.id)
                .posterId(this.posterId)
                .text(this.text)
                .postTimestamp(this.postTimestamp)
                .lastEditTimestamp(this.lastEditTimestamp)
                .build();
    }
}
