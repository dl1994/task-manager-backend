package at.doml.taskmanager.config;

import at.doml.taskmanager.components.AuthenticationHandlers;
import at.doml.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    private final AuthenticationHandlers authenticationHandlers;
    
    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder, SessionRegistry sessionRegistry,
                          AuthenticationHandlers authenticationHandlers) {
        this.passwordEncoder = passwordEncoder;
        this.sessionRegistry = sessionRegistry;
        this.authenticationHandlers = authenticationHandlers;
    }
    
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public static SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserService userService) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(this.passwordEncoder);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement()
                .maximumSessions(1)
                .expiredSessionStrategy(SecurityConfig::expiredSessionHandler)
                .sessionRegistry(this.sessionRegistry);
        http.formLogin()
                .loginProcessingUrl("/login")
                .successHandler(this.authenticationHandlers)
                .failureHandler(this.authenticationHandlers)
                .permitAll();
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(this.authenticationHandlers)
                .deleteCookies("JSESSIONID")
                .permitAll();
        http.authorizeRequests().antMatchers("/status", "/")
                .permitAll().anyRequest().authenticated();
        http.httpBasic();
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
    }
    
    private static AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
    
    private static void expiredSessionHandler(SessionInformationExpiredEvent event) {
        event.getResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
