/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * The MIT License (MIT)                                                           *
 *                                                                                 *
 * Copyright © 2017 Domagoj Latečki                                                *
 *                                                                                 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy    *
 * of this software and associated documentation files (the "Software"), to deal   *
 * in the Software without restriction, including without limitation the rights    *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell       *
 * copies of the Software, and to permit persons to whom the Software is           *
 * furnished to do so, subject to the following conditions:                        *
 *                                                                                 *
 * The above copyright notice and this permission notice shall be included in all  *
 * copies or substantial portions of the Software.                                 *
 *                                                                                 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR      *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,        *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE     *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER          *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,   *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE   *
 * SOFTWARE.                                                                       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package at.dom_l.task_manager.models.db;

import at.dom_l.task_manager.models.resp.TaskResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private static final int MAX_SUBJECT_LENGTH = 50;

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Integer priority;
    @Column(nullable = false, length = MAX_SUBJECT_LENGTH)
    private String subject;
    @Column
    private Integer assigneeId; // TODO: add constraint
    @Column(nullable = false)
    private Integer projectId; // TODO: add constraint
    @Column(nullable = false)
    private Long createdTimestamp;
    @Column
    private Long startedTimestamp;
    @Column
    private Long dueTimestamp;
    @Column
    private Long finishedTimestamp;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    
    public enum Status {
        NEW, IN_PROGRESS, DONE
    }
    
    public TaskResp toResp() {
        return TaskResp.builder()
                .id(this.id)
                .priority(this.priority)
                .subject(this.subject)
                .assigneeId(this.assigneeId)
                .createdTimestamp(this.createdTimestamp)
                .startedTimestamp(this.startedTimestamp)
                .dueTimestamp(this.dueTimestamp)
                .finishedTimestamp(this.finishedTimestamp)
                .status(this.status)
                .build();
    }
}
