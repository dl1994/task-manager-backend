package at.dom_l.task_manager.util;

import lombok.Getter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public class ControllerInfo {
    
    private final String path;
    private final String requestMethod;
    private final String controllerName;
    private final Type requestBodyType;
    private final Type responseBodyType;
    private final Type[] modelAttributes;
    private final Type[] pathVariables;
    // TODO improve this
    
    public ControllerInfo(Map.Entry<RequestMappingInfo, HandlerMethod> requestMapping) {
        RequestMappingInfo requestMappingInfo = requestMapping.getKey();
        HandlerMethod handlerMethod = requestMapping.getValue();
        
        this.controllerName = handlerMethod.getBeanType().getSimpleName();
        this.pathVariables = Stream.of(handlerMethod.getMethodParameters())
                .filter(p -> p.hasParameterAnnotation(PathVariable.class))
                .map(MethodParameter::getGenericParameterType)
                .toArray(Type[]::new);
        this.modelAttributes = Stream.of(handlerMethod.getMethodParameters())
                .filter(p -> p.hasParameterAnnotation(ModelAttribute.class))
                .map(MethodParameter::getGenericParameterType)
                .toArray(Type[]::new);
        
        Optional<MethodParameter> optRequestParameter = Stream.of(handlerMethod.getMethodParameters())
                .filter(p -> p.hasParameterAnnotation(RequestBody.class))
                .findFirst();
        
        this.requestBodyType = optRequestParameter.map(MethodParameter::getGenericParameterType).orElse(null);
        
        Optional<MethodParameter> optResponseParameter = Optional.of(handlerMethod.getReturnType())
                .filter(p -> {
                    Class<?> type = p.getParameterType();
                    return !Objects.equals(void.class, type) && !Objects.equals(Void.class, type);
                });
        
        this.responseBodyType = optResponseParameter.map(MethodParameter::getGenericParameterType).orElse(null);
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
        return "<div>\n" +
                "<div class=\"header\"><div class=\"header-field method\">"
                + this.requestMethod + "</div><div class=\"header-field url\">" + this.path + "</div></div>\n" +
                "<div class=\"table\">" +
                (this.pathVariables.length != 0
                        ? "<div class=\"row\"><div class=\"row-field attribute\">PATH VARIABLES</div>" +
                        "<div class=\"row-field\"><code>"
                        + TypeWriter.stringifyGetters(this.pathVariables[0])
                        + "</code></div></div>\n"
                        : ""
                ) +
                (this.modelAttributes.length != 0
                        ? "<div class=\"row\"><div class=\"row-field attribute\">QUERY PARAMETERS</div>" +
                        "<div class=\"row-field\"><code>"
                        + TypeWriter.stringifyGetters(this.modelAttributes[0])
                        + "</code></div></div>\n"
                        : ""
                ) +
                (this.requestBodyType != null
                        ? "<div class=\"row\"><div class=\"row-field attribute\">REQUEST BODY</div>" +
                        "<div class=\"row-field\"><code>"
                        + TypeWriter.stringifySetters(this.requestBodyType)
                        + "</code></div></div>\n"
                        : ""
                ) +
                (this.responseBodyType != null
                        ? "<div class=\"row\"><div class=\"row-field attribute\">RESPONSE BODY</div>" +
                        "<div class=\"row-field\"><code>"
                        + TypeWriter.stringifyGetters(this.responseBodyType)
                        + "</code></div></div>\n"
                        : ""
                ) +
                "</div></div><br/>\n";
    }
}
