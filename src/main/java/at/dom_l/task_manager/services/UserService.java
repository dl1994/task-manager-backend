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
import java.util.Optional;
import java.util.function.Consumer;

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
                .orElseThrow(() -> new UserNotFoundException("no user with id: " + userId));
    }
}
