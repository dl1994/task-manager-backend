package at.dom_l.task_manager.models.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationQueryParams {
    
    private static final Integer DEFAULT_PAGE_SIZE = 50;
    
    private Integer page = 0;
    private Integer itemsPerPage = DEFAULT_PAGE_SIZE;
}
