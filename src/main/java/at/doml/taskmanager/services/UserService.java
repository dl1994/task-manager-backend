package at.doml.taskmanager.services;

import at.doml.taskmanager.dao.UserDao;
import at.doml.taskmanager.exceptions.PasswordException;
import at.doml.taskmanager.exceptions.UserNotFoundException;
import at.doml.taskmanager.models.db.User;
import at.doml.taskmanager.models.req.NewUserReq;
import at.doml.taskmanager.models.req.UserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class UserService implements UserDetailsService {
    
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    private static final int MIN_PASSWORD_LENGTH = 4;
    
    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder, SessionRegistry sessionRegistry) {
        this.userDao = userDao;
        this.sessionRegistry = sessionRegistry;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userDao.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("no user with username: " + username));
    }
    
    @Transactional(readOnly = true)
    public Optional<User> loadUserById(Integer id) {
        return this.userDao.getByPrimaryKey(id);
    }
    
    private String encoded(String password) {
        if (password.length() >= MIN_PASSWORD_LENGTH) {
            return this.passwordEncoder.encode(password);
        } else {
            throw new PasswordException();
        }
    }
    
    @Transactional
    public Integer createUser(NewUserReq newUserReq) {
        return this.userDao.create(
                User.builder()
                        .username(newUserReq.getUsername())
                        .firstName(newUserReq.getFirstName())
                        .lastName(newUserReq.getLastName())
                        .role(newUserReq.getRole())
                        .password(this.encoded(newUserReq.getPassword()))
                        .build()
        );
    }
    
    @Transactional
    public void deleteUser(Integer userId) {
        // TODO un-own all tasks, delete comments, etc.
        this.userDao.delete(this.getUserById(userId));
    }
    
    @Transactional
    public void changeUserInfo(Integer userId, UserReq userReq) {
        this.updateUser(userId, user -> {
            user.setFirstName(userReq.getFirstName());
            user.setLastName(userReq.getLastName());
        });
    }
    
    @Transactional
    public void changePassword(Integer userId, String newPassword) {
        this.updateUser(userId, user -> user.setPassword(this.encoded(newPassword)));
    }
    
    @Transactional
    public boolean checkPassword(Integer userId, String inputedPassword) {
        return this.loadUserById(userId).map(User::getPassword)
                .map(actualPassword -> this.passwordEncoder.matches(inputedPassword, actualPassword))
                .orElse(false);
    }
    
    @Transactional
    public void changeRole(Integer userId, User.Role newRole) {
        this.updateUser(userId, user -> user.setRole(newRole));
    }
    
    private void updateUser(Integer userId, Consumer<User> updater) {
        User user = this.getUserById(userId);
        updater.accept(user);
        this.userDao.update(user);
    }
    
    private User getUserById(Integer userId) {
        return this.userDao.getByPrimaryKey(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    public void expireSessions(Integer userId) {
        this.sessionRegistry.getAllPrincipals()
                .stream()
                .map(User.class::cast)
                .filter(principal -> Objects.equals(principal.getId(), userId))
                .findFirst()
                .map(principal -> this.sessionRegistry.getAllSessions(principal, false))
                .ifPresent(sessions -> sessions.forEach(SessionInformation::expireNow));
    }
}
