package at.doml.taskmanager.controllers;

import at.doml.taskmanager.util.ControllerInfo;
import at.doml.taskmanager.models.db.Notification;
import at.doml.taskmanager.models.db.User;
import at.doml.taskmanager.models.req.NewUserReq;
import at.doml.taskmanager.models.req.NotificationReq;
import at.doml.taskmanager.services.NotificationService;
import at.doml.taskmanager.services.UserService;
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
    
    private static final String CSS = "body {\n" +
            "    font-family: sans-serif;\n" +
            "    margin: 0;\n" +
            "    background: #DDDDDD;\n" +
            "}\n\n" +
            "h1 {\n" +
            "    color: #444444;\n" +
            "}\n\n" +
            ".container {\n" +
            "    position: relative;\n" +
            "    left: 12.5%;\n" +
            "    width: calc(75% - 40pt);\n" +
            "    border: 1px solid gray;\n" +
            "    padding: 20pt;\n" +
            "    padding-top: 1px;\n" +
            "    background: white;\n" +
            "    margin-top: 25pt;\n" +
            "    margin-bottom: 25pt;\n" +
            "}\n\n" +
            ".header {\n" +
            "    width: 100%;\n" +
            "    border: 1px solid gray;\n" +
            "    border-radius: 3pt;\n" +
            "    overflow: hidden;\n" +
            "    background: #F0F0F0;\n" +
            "}\n\n" +
            ".header-field {\n" +
            "    padding: 5pt;\n" +
            "    display: inline-block;\n" +
            "    font-weight: bold;\n" +
            "}\n\n" +
            ".method {\n" +
            "    border-right: 1px solid gray;\n" +
            "    background: linear-gradient(#EEEEEE,#999999);\n" +
            "    border-radius: 3pt;\n" +
            "    color: #505050;\n" +
            "}\n\n" +
            ".table {\n" +
            "    width: 100%;\n" +
            "    display: table;\n" +
            "}\n\n" +
            ".url {\n" +
            "    color: #606060;\n" +
            "}\n\n" +
            ".row {\n" +
            "    display: table-row;\n" +
            "}\n\n" +
            ".row-field {\n" +
            "    padding: 5pt;\n" +
            "    display: table-cell;\n" +
            "    vertical-align: middle;\n" +
            "    border-bottom: 1px solid gray;\n" +
            "}\n\n" +
            ".attribute {\n" +
            "    border-right: 1px solid gray;\n" +
            "}\n\n" +
            "b.property {\n" +
            "    color: #660E7A;\n" +
            "}\n\n" +
            "b.string {\n" +
            "    color: #008000;\n" +
            "}\n\n" +
            "b.number {\n" +
            "    font-weight: normal;\n" +
            "    color: #0000FF;\n" +
            "}\n\n" +
            "b.keyword {\n" +
            "    color: #000080;\n" +
            '}';
    
    @RequestMapping(value = "/", method = GET)
    public String getApiReference() {
        Map<RequestMappingInfo, HandlerMethod> requestMappings = this.handlerMapping.getHandlerMethods();
        Map<String, List<ControllerInfo>> controllers = requestMappings.entrySet()
                .stream()
                .map(ControllerInfo::new)
                .filter(ai -> !Objects.equals(ai.getControllerName(), AppController.class.getSimpleName()))
                .collect(Collectors.groupingBy(ControllerInfo::getControllerName));
        StringBuilder responseBuilder = new StringBuilder();
        
        responseBuilder.append("<head><style>")
                .append(CSS)
                .append("</style></head><body><div class=\"container\">");
        controllers.forEach((controllerFullName, controllerInfo) -> {
            String apiSectionName = SPLIT_PATTERN.matcher(controllerFullName.replace("Controller", ""))
                    .replaceAll(REPLACEMENT_STRING);
            
            responseBuilder.append("<h1>")
                    .append(apiSectionName)
                    .append("</h1>\n");
            controllerInfo.forEach(responseBuilder::append);
        });
        
        responseBuilder.append("</div></body>");
        
        return responseBuilder.toString();
    }
}
