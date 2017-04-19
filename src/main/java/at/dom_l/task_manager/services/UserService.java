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
package at.dom_l.task_manager.services;

import at.dom_l.task_manager.dao.UserDao;
import at.dom_l.task_manager.exceptions.PasswordException;
import at.dom_l.task_manager.exceptions.UserNotFoundException;
import at.dom_l.task_manager.models.db.Comment;
import at.dom_l.task_manager.models.db.Notification;
import at.dom_l.task_manager.models.db.Project;
import at.dom_l.task_manager.models.db.Task;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.req.NewUserReq;
import at.dom_l.task_manager.models.req.UserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class UserService implements UserDetailsService {
    
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private static final int MIN_PASSWORD_LENGTH = 4;
    
    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userDao.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("no user with username " + username));
    }
    
    @Transactional(readOnly = true)
    public Optional<User> loadUserById(Integer id) {
        return this.userDao.getUserById(id);
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
        User user = new User();
        
        user.setUsername(newUserReq.getUsername());
        user.setFirstName(newUserReq.getFirstName());
        user.setLastName(newUserReq.getLastName());
        user.setRole(newUserReq.getRole());
        user.setPassword(this.encoded(newUserReq.getPassword()));
        
        return this.userDao.create(user);
    }
    
    @Transactional
    public void deleteUser(Integer userId) {
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
        return this.userDao.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("no user with id " + userId));
    }
    
    @Transactional(readOnly = true)
    public List<Task> getOwnedTasks(Integer userId) {
        return this.getList(userId, User::getOwnedTasks);
    }
    
    @Transactional(readOnly = true)
    public List<Task> getAssignedTasks(Integer userId) {
        return this.getList(userId, User::getAssignedTasks);
    }
    
    @Transactional(readOnly = true)
    public List<Project> getOwnedProjects(Integer userId) {
        return this.getList(userId, User::getOwnedProjects);
    }
    
    @Transactional(readOnly = true)
    public List<Project> getAssignedProjects(Integer userId) {
        return this.getList(userId, User::getAssignedProjects);
    }
    
    @Transactional(readOnly = true)
    public List<Comment> getComments(Integer userId) {
        return this.getList(userId, User::getComments);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Integer userId) {
        return this.getList(userId, User::getNotifications);
    }
    
    private <T> List<T> getList(Integer userId, Function<User, List<T>> listGetter) {
        return listGetter.apply(this.getUserById(userId));
    }
}
