package at.dom_l.task_manager.components;

import at.dom_l.task_manager.models.db.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationHandlers implements AuthenticationSuccessHandler,
        AuthenticationFailureHandler, LogoutSuccessHandler {
    
    private final ObjectMapper objectMapper;
    
    @Autowired
    public AuthenticationHandlers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User principal = (User) authentication.getPrincipal();
        String userJson = this.objectMapper.writeValueAsString(principal.toResp());
        
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(userJson);
        // TODO: 10.01.17. LOG?
        // TODO: 10.01.17. CONTENT TYPE?
    }
    
    @Override
    @Transactional
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) {
        response.setStatus(HttpServletResponse.SC_OK);
        // TODO: 10.01.17. LOG?
    }
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
