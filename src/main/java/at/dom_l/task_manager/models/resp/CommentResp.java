package at.dom_l.task_manager.models.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResp {
    
    private Integer id;
    private Integer posterId;
    private String text;
    private Long postTimestamp;
    private Long lastEditTimestamp;
}
