package at.dom_l.task_manager.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypeWriter {
    
    //
    // Constants
    //
    private static final String OBJECT_STRING = "{}";
    private static final String NUMBER_STRING = "<b class=\"number\">0</b>";
    private static final String DECIMAL_STRING = "<b class=\"number\">0.0</b>";
    private static final String BOOLEAN_STRING = "<b class=\"keyword\">true</b> | <b class=\"keyword\">false</b>";
    private static final String STRING_STRING = "<b class=\"string\">\"string\"</b>";
    private static final String CHARACTER_STRING = "<b class=\"string\">'c'</b>";
    private static final Map<String, String> BASIC_WRITERS = new HashMap<>();
    private static final CustomClassFieldFetcher GETTER_FETCHER = new GetterFieldFetcher();
    private static final CustomClassFieldFetcher SETTER_FETCHER = new SetterFieldFetcher();
    
    //
    // Static initializers
    //
    static {
        addBasicWriterFor(OBJECT_STRING, Object.class);
        addBasicWriterFor(NUMBER_STRING,
                Byte.class, Short.class, Integer.class, Long.class, BigInteger.class,
                byte.class, short.class, int.class, long.class
        );
        addBasicWriterFor(DECIMAL_STRING,
                Float.class, Double.class, BigDecimal.class,
                float.class, double.class
        );
        addBasicWriterFor(BOOLEAN_STRING,
                Boolean.class,
                boolean.class
        );
        addBasicWriterFor(STRING_STRING, String.class);
        addBasicWriterFor(CHARACTER_STRING,
                Character.class,
                char.class
        );
    }
    
    //
    // Private classes and interfaces
    //
    private interface CustomClassFieldFetcher {
        boolean canFetchFrom(Method method);
        
        TypeParameterInformation fetchField(Method method);
    }
    
    private static class GetterFieldFetcher implements CustomClassFieldFetcher {
        
        @Override
        public boolean canFetchFrom(Method method) {
            String name = method.getName();
            return method.getParameters().length == 0 && !isVoid(method) && !Objects.equals(name, "getClass")
                    && (name.startsWith("get") || name.startsWith("is"));
        }
        
        @Override
        public TypeParameterInformation fetchField(Method method) {
            return new TypeParameterInformation(method.getGenericReturnType().getTypeName());
        }
    }
    
    private static class SetterFieldFetcher implements CustomClassFieldFetcher {
        
        @Override
        public boolean canFetchFrom(Method method) {
            return method.getParameters().length == 1 && isVoid(method) && method.getName().startsWith("set");
        }
        
        @Override
        public TypeParameterInformation fetchField(Method method) {
            return new TypeParameterInformation(method.getGenericParameterTypes()[0].getTypeName());
        }
    }
    
    //
    // Static utility methods
    //
    private static boolean isVoid(Method method) {
        Class<?> returnType = method.getReturnType();
        return Objects.equals(returnType, void.class) || Objects.equals(returnType, Void.class);
    }
    
    private static String getFieldName(String methodName) {
        String withoutGetterSetterPrefix = methodName.replaceAll("^(get|is|set)", "");
        
        if (withoutGetterSetterPrefix.length() > 1) {
            return Character.toLowerCase(withoutGetterSetterPrefix.charAt(0)) + withoutGetterSetterPrefix.substring(1);
        }
        
        return withoutGetterSetterPrefix.toLowerCase();
    }
    
    private static void addBasicWriterFor(String value, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            BASIC_WRITERS.put(clazz.getTypeName(), value);
        }
    }
    
    private static String indent(int indentLevel) {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (int i = 0; i < indentLevel; i++) {
            stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        
        return stringBuilder.toString();
    }
    
    //
    // TODO: this will be instance methods
    //
    public static String stringifyGetters(Type type) {
        return stringify(type, GETTER_FETCHER);
    }
    
    public static String stringifySetters(Type type) {
        return stringify(type, SETTER_FETCHER);
    }
    
    private static String stringify(Type type, CustomClassFieldFetcher fieldFetcher) {
        return stringify(new TypeParameterInformation(type.getTypeName()), fieldFetcher, 1);
    }
    
    private static String stringify(TypeParameterInformation typeParameterInformation,
                                    CustomClassFieldFetcher fieldFetcher, int indentLevel) {
        if (typeParameterInformation.isArray()) {
            return writeArray(typeParameterInformation, fieldFetcher, indentLevel);
        }
        
        String basic = BASIC_WRITERS.get(typeParameterInformation.getType());
        
        if (basic != null) {
            return basic;
        }
        
        Class<?> clazz;
        
        try {
            clazz = Class.forName(typeParameterInformation.getType());
        } catch (ClassNotFoundException ignored) {
            return "?";
        }
        
        if (clazz.isEnum()) {
            return writeEnum(clazz);
        }
        
        if (Collection.class.isAssignableFrom(clazz)) {
            return writeCollection(typeParameterInformation, fieldFetcher, indentLevel);
        }
        
        if (Map.class.isAssignableFrom(clazz)) {
            return writeMap(typeParameterInformation, fieldFetcher, indentLevel);
        }
        
        return writeClass(clazz, typeParameterInformation, fieldFetcher, indentLevel);
    }
    
    private static String writeEnum(Class<?> clazz) {
        Object[] constants = clazz.getEnumConstants();
        StringBuilder stringBuilder = new StringBuilder("enum(");
        
        for (Object constant : constants) {
            stringBuilder.append("<b class=\"string\">\"")
                    .append(((Enum<?>) constant).name())
                    .append("\"</b>")
                    .append(", ");
        }
        
        if (constants.length != 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        
        return stringBuilder.append(')').toString();
    }
    
    private static String writeCollection(TypeParameterInformation typeParameterInformation,
                                          CustomClassFieldFetcher fieldFetcher, int indentLevel) {
        return '[' + stringify(typeParameterInformation.getTypeParameters()[0], fieldFetcher, indentLevel) + ", ...]";
    }
    
    private static String writeArray(TypeParameterInformation typeParameterInformation,
                                     CustomClassFieldFetcher fieldFetcher, int indentLevel) {
        return '[' + stringify(new TypeParameterInformation(
                typeParameterInformation.getType(), typeParameterInformation.getTypeParameters(),
                typeParameterInformation.getArrayDimension() - 1
        ), fieldFetcher, indentLevel) + ", ...]";
    }
    
    private static String writeMap(TypeParameterInformation typeParameterInformation,
                                   CustomClassFieldFetcher fieldFetcher, int indentLevel) {
        TypeParameterInformation[] typeParameters = typeParameterInformation.getTypeParameters();
        TypeParameterInformation keyType = typeParameters[0];
        TypeParameterInformation valueType = typeParameters[1];
        String indent = indent(indentLevel);
        
        return "{<br/>" + indent +
                stringify(keyType, fieldFetcher, indentLevel + 1) +
                ": " +
                stringify(valueType, fieldFetcher, indentLevel + 1) +
                ',' +
                "<br/>" + indent + "...<br/>" + indent(indentLevel - 1) + '}';
    }
    
    private static String writeClass(Class<?> clazz, TypeParameterInformation typeParameterInformation,
                                     CustomClassFieldFetcher fieldFetcher, int indentLevel) {
        Field[] publicFields = clazz.getFields();
        Method[] publicMethods = clazz.getMethods();
        Map<String, String> fields = new HashMap<>();
        
        if (typeParameterInformation.hasTypeParameters()) {
            // TODO handle generic fields
        }
        
        for (Field publicField : publicFields) {
            fields.put(publicField.getName(), stringify(
                    new TypeParameterInformation(publicField.getGenericType().getTypeName()),
                    fieldFetcher, indentLevel + 1
            ));
        }
        
        for (Method publicMethod : publicMethods) {
            if (fieldFetcher.canFetchFrom(publicMethod)) {
                fields.put(getFieldName(publicMethod.getName()), stringify(
                        fieldFetcher.fetchField(publicMethod), fieldFetcher, indentLevel + 1
                )); // TODO generic
            }
        }
        
        StringBuilder stringBuilder = new StringBuilder()
                .append('{');
        String indent = indent(indentLevel);
        
        fields.forEach((name, value) -> {
            stringBuilder.append("<br/>")
                    .append(indent)
                    .append("<b class=\"property\">\"")
                    .append(name)
                    .append("\"</b>: ")
                    .append(value)
                    .append(',');
        });
        
        if (stringBuilder.length() > 1) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            stringBuilder.append("<br>");
        }
        
        stringBuilder.append(indent(indentLevel - 1));
        stringBuilder.append('}');
        
        return stringBuilder.toString();
    }
}
