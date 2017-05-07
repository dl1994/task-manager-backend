package at.dom_l.task_manager.controllers;

import at.dom_l.task_manager.models.ControllerInfo;
import at.dom_l.task_manager.models.db.Notification;
import at.dom_l.task_manager.models.db.User;
import at.dom_l.task_manager.models.req.NewUserReq;
import at.dom_l.task_manager.models.req.NotificationReq;
import at.dom_l.task_manager.services.NotificationService;
import at.dom_l.task_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class AppController {
    
    private final RequestMappingHandlerMapping handlerMapping;
    private static final String REPLACEMENT_STRING = "$1 $2";
    private static final Pattern SPLIT_PATTERN = Pattern.compile("(.)(\\p{javaUpperCase})");
    
    @Autowired
    public AppController(UserService userService, NotificationService notificationService,
                         RequestMappingHandlerMapping handlerMapping) {
        Integer userId = userService.createUser(NewUserReq.builder()
                .firstName("Test")
                .lastName("Admin")
                .username("admin")
                .password("pass")
                .role(User.Role.ROLE_ADMIN)
                .build());
        notificationService.createNotification(NotificationReq.builder()
                .userId(userId)
                .type(Notification.Type.COMMENT)
                .text("test text 1")
                .target(0)
                .build()
        );
        notificationService.createNotification(NotificationReq.builder()
                .userId(userId)
                .type(Notification.Type.COMMENT)
                .text("test text 2")
                .target(0)
                .build()
        );
        userService.createUser(NewUserReq.builder()
                .firstName("Test")
                .lastName("User")
                .username("user")
                .password("pass")
                .role(User.Role.ROLE_USER)
                .build());
        this.handlerMapping = handlerMapping;
    }
    
    @RequestMapping(value = "/status", method = GET)
    public void status() {}
    
    @RequestMapping(value = "/", method = GET)
    public String getApiReference() {
        Map<RequestMappingInfo, HandlerMethod> requestMappings = this.handlerMapping.getHandlerMethods();
        Map<String, List<ControllerInfo>> controllers = requestMappings.entrySet()
                .stream()
                .map(ControllerInfo::new)
                .filter(ai -> !Objects.equals(ai.getControllerName(), AppController.class.getSimpleName()))
                .collect(Collectors.groupingBy(ControllerInfo::getControllerName));
        StringBuilder responseBuilder = new StringBuilder();
        
        controllers.forEach((controllerFullName, controllerInfo) -> {
            String apiSectionName = SPLIT_PATTERN.matcher(controllerFullName.replace("Controller", ""))
                    .replaceAll(REPLACEMENT_STRING);
            
            responseBuilder.append("<h1>")
                    .append(apiSectionName)
                    .append("</h1><br/>\n");
            controllerInfo.forEach(responseBuilder::append);
        });
        
        return responseBuilder.toString();
    }
}
