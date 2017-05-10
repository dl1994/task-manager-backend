package at.doml.taskmanager.config;

import at.doml.taskmanager.components.AuthenticationHandlers;
import at.doml.taskmanager.controllers.AppController;
import at.doml.taskmanager.dao.UserDao;
import at.doml.taskmanager.services.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import java.util.List;

@Configuration
@Import({SecurityConfig.class, DataConfig.class})
@ComponentScan(basePackageClasses = {
        UserDao.class,
        UserService.class,
        AppController.class,
        AuthenticationHandlers.class
})
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(this.objectMapper()));
        this.addDefaultHttpMessageConverters(converters);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    @Bean
    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver(){
        return new AuthenticationPrincipalArgumentResolver();
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(this.authenticationPrincipalArgumentResolver());
    }
}
