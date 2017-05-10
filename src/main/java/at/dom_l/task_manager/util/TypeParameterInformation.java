package at.dom_l.task_manager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TypeParameterInformation {
    
    private final int arrayDimension;
    private final String type;
    private final TypeParameterInformation[] typeParameters;
    
    private static final Pattern ARRAY_REMOVAL_PATTERN = Pattern.compile("(\\[])*$");
    private static final Pattern CLOSING_DIAMOND_REMOVAL_PATTERN = Pattern.compile(">(\\[])*$");
    
    public TypeParameterInformation(String type, Map<String, String> typeParameterMappings) {
        String trimmedType = type.trim();
        String[] split = trimmedType.split("<", 2);
        
        this.arrayDimension = findArrayDimension(trimmedType);
        this.type = extractTypeFromMap(this.arrayDimension > 0
                ? ARRAY_REMOVAL_PATTERN.matcher(split[0]).replaceAll("")
                : split[0], typeParameterMappings);
        this.typeParameters = split.length == 1
                ? new TypeParameterInformation[0]
                : (Arrays.stream(splitTypeParameters(
                CLOSING_DIAMOND_REMOVAL_PATTERN.matcher(split[1]).replaceAll("")))
                .map(TypeParameterInformation::new)
                .toArray(TypeParameterInformation[]::new));
    }
    
    public TypeParameterInformation(String type) {
        this(type, Collections.emptyMap());
    }
    
    public TypeParameterInformation(String type, TypeParameterInformation[] typeParameters, int arrayDimension) {
        this.arrayDimension = arrayDimension;
        this.type = type;
        this.typeParameters = typeParameters.clone();
    }
    
    private static String extractTypeFromMap(String key, Map<String, String> typeParameterMappings) {
        return typeParameterMappings.getOrDefault(key, key);
    }
    
    private static int findArrayDimension(String type) {
        char[] chars = type.toCharArray();
        int index = chars.length - 1;
        int dimension = 0;
        boolean endsWithCorrectChar = true;
        
        while (endsWithCorrectChar && index >= 0) {
            if (chars[index] == ']') {
                index -= 2;
                dimension++;
            } else {
                endsWithCorrectChar = false;
            }
        }
        
        return dimension;
    }
    
    private static String[] splitTypeParameters(String typeParameters) {
        List<Integer> splitPoints = new ArrayList<>();
        
        splitPoints.add(-1);
        
        int currentDepth = 0;
        char[] chars = typeParameters.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            if (currentDepth == 0 && chars[i] == ',') {
                splitPoints.add(i);
            } else if (chars[i] == '<') {
                currentDepth++;
            } else if (chars[i] == '>') {
                currentDepth--;
            }
        }
        
        splitPoints.add(typeParameters.length());
        
        String[] splitTypeParameters = new String[splitPoints.size() - 1];
        
        for (int i = 0; i < splitTypeParameters.length; i++) {
            splitTypeParameters[i] = typeParameters.substring(splitPoints.get(i) + 1, splitPoints.get(i + 1));
        }
        
        return splitTypeParameters;
    }
    
    public String getType() {
        return this.type;
    }
    
    public TypeParameterInformation[] getTypeParameters() {
        return this.typeParameters.clone();
    }
    
    public boolean isArray() {
        return this.arrayDimension > 0;
    }
    
    public int getArrayDimension() {
        return this.arrayDimension;
    }
}
