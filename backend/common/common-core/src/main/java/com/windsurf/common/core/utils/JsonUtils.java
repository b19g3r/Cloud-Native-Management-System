package com.windsurf.common.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON utility class using Jackson
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Configure ObjectMapper
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Object to JSON string
     */
    public static String toJsonString(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Input object cannot be null");
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Convert object to JSON string failed", e);
            throw new RuntimeException("Failed to convert object to JSON string", e);
        }
    }

    /**
     * JSON string to object
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        if (jsonString == null || clazz == null) {
            throw new IllegalArgumentException("JSON string and class type cannot be null");
        }
        if (jsonString.isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be empty");
        }
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.error("Parse JSON string to object failed", e);
            throw new RuntimeException("Failed to parse JSON string to object", e);
        }
    }

    /**
     * JSON string to List
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        if (jsonString == null || clazz == null) {
            throw new IllegalArgumentException("JSON string and class type cannot be null");
        }
        if (jsonString.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            log.error("Parse JSON string to array failed", e);
            throw new RuntimeException("Failed to parse JSON string to array", e);
        }
    }

    /**
     * JSON string to Map
     */
    public static Map<String, Object> parseMap(String jsonString) {
        if (jsonString == null) {
            throw new IllegalArgumentException("JSON string cannot be null");
        }
        if (jsonString.isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be empty");
        }
        try {
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            log.error("Parse JSON string to map failed", e);
            throw new RuntimeException("Failed to parse JSON string to map", e);
        }
    }

    /**
     * JSON string to JsonNode
     */
    public static JsonNode parseNode(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(jsonString);
        } catch (IOException e) {
            log.error("Parse JSON string to JsonNode failed", e);
            return null;
        }
    }

    /**
     * Create empty JsonNode
     */
    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    /**
     * Deep copy object using JSON serialization
     */
    public static <T> T deepCopy(T object, Class<T> clazz) {
        if (object == null || clazz == null) {
            throw new IllegalArgumentException("Object and class type cannot be null");
        }
        try {
            String jsonString = toJsonString(object);
            return parseObject(jsonString, clazz);
        } catch (Exception e) {
            log.error("Deep copy object failed", e);
            throw new RuntimeException("Failed to deep copy object", e);
        }
    }

    /**
     * Check if string is valid JSON
     */
    public static boolean isValidJson(String jsonString) {
        if (jsonString == null) {
            throw new IllegalArgumentException("JSON string cannot be null");
        }
        if (jsonString.isEmpty()) {
            return false;
        }
        try {
            objectMapper.readTree(jsonString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Pretty print JSON string
     */
    public static String prettyPrint(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            Object obj = objectMapper.readValue(jsonString, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.error("Pretty print JSON string failed", e);
            return jsonString;
        }
    }

    /**
     * Convert object to Map
     */
    public static Map<String, Object> toMap(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Input object cannot be null");
        }
        try {
            String jsonString = toJsonString(object);
            return parseMap(jsonString);
        } catch (Exception e) {
            log.error("Convert object to map failed", e);
            throw new RuntimeException("Failed to convert object to map", e);
        }
    }

    /**
     * Get ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}