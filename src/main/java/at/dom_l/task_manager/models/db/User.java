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
@Table(indexes = {
        @Index(columnList = "role"),
        @Index(columnList = "username")
})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_HASHED_PASSWORD_LENGTH = 80;
    private static final long serialVersionUID = -3486687607400525759L;
    
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true, nullable = false, length = MAX_NAME_LENGTH)
    private String username;
    @Column(length = MAX_NAME_LENGTH)
    private String firstName;
    @Column(length = MAX_NAME_LENGTH)
    private String lastName;
    @Column(nullable = false, length = MAX_HASHED_PASSWORD_LENGTH)
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private Role role;
    
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
    
    public UserResp toResp() {
        return UserResp.builder()
                .id(this.id)
                .username(this.username)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .role(this.role)
                .build();
    }
}
