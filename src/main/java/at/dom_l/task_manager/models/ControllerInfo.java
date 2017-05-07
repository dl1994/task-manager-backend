package at.dom_l.task_manager.models;

import lombok.Getter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public class ControllerInfo {
    
    private final String path;
    private final String requestMethod;
    private final String controllerName;
    private final Class<?> requestBodyClass;
    private final Class<?> responseBodyClass;
    // TODO improve this
    
    public ControllerInfo(Map.Entry<RequestMappingInfo, HandlerMethod> requestMapping) {
        RequestMappingInfo requestMappingInfo = requestMapping.getKey();
        HandlerMethod handlerMethod = requestMapping.getValue();
        
        this.controllerName = handlerMethod.getBeanType().getSimpleName();
        Optional<Class<?>> optRequestBodyClass = Stream.of(handlerMethod.getMethodParameters())
                .filter(p -> p.hasParameterAnnotation(RequestBody.class))
                .findFirst()
                .map(MethodParameter::getParameterType);
        this.requestBodyClass = optRequestBodyClass.orElse(void.class);
        this.responseBodyClass = handlerMethod.getReturnType()
                .getParameterType();
        this.path = requestMappingInfo.getPatternsCondition()
                .getPatterns()
                .stream()
                .findFirst()
                .orElse(null);
        this.requestMethod = requestMappingInfo.getMethodsCondition()
                .getMethods()
                .stream()
                .findFirst()
                .map(RequestMethod::name)
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return "<table border=\"1\">\n" +
                "<tr><th>" + this.requestMethod + "</th><th>" + this.path + "</th></tr>\n" +
                (!Objects.equals(this.requestBodyClass, void.class)
                        ? "<tr><td>Request body:</td><td>" + this.requestBodyClass.getSimpleName() + "</td></tr>\n"
                        : "") +
                (!Objects.equals(this.responseBodyClass, void.class)
                        ? "<tr><td>Response body:</td><td>" + this.responseBodyClass.getSimpleName() + "</td></tr>\n"
                        : "") +
                "</table><br/>\n";
    }
}
