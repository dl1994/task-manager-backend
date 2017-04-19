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

import at.dom_l.task_manager.models.resp.UserResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;

@Data
@Table(indexes = @Index(columnList = "username"))
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private static final int MAX_NAME_LENGTH = 30;
    private static final long serialVersionUID = 7717753194949904456L;
    
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true, nullable = false, length = MAX_NAME_LENGTH)
    private String username;
    @Column(length = MAX_NAME_LENGTH)
    private String firstName;
    @Column(length = MAX_NAME_LENGTH)
    private String lastName;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private Role role;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
//    private List<Task> ownedTasks;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assignee")
//    private List<Task> assignedTasks;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
//    private List<Project> ownedProjects;
//    @ManyToMany(mappedBy = "involvedUsers")
//    private List<Project> assignedProjects;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "poster")
//    private List<Comment> comments;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
//    private List<Notification> notifications;

    public UserResp toResp() {
        return UserResp.builder()
                .id(this.id)
                .username(this.username)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .role(this.role)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(this.role.toAuthority());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public enum Role {
        
        ROLE_ADMIN, ROLE_USER;
        
        public GrantedAuthority toAuthority() {
            return this::name;
        }
    }
}
