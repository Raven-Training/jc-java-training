package com.raven.training.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom deserializer to handle identifiers that can be represented as
 * either a single string or an array of strings in a JSON object.
 *
 * This deserializer processes a JSON object and, for each key-value pair,
 * it extracts the value. If the value is an array, it takes the first
 * element and converts it to a string. Otherwise, it converts the value
 * directly to a string. The result is a simple map of strings.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
public class IdentifierDeserializer extends JsonDeserializer<Map<String, Object>> {

    /**
     * Deserializes a JSON object, handling cases where values are single strings
     * or arrays of strings. It extracts the first element from any array values.
     *
     * @param p The {@link JsonParser} used to read the JSON content.
     * @param ctxt The context for the deserialization process.
     * @return A {@link Map} where the keys are strings and the values are the
     * deserialized identifiers as strings.
     * @throws IOException if an I/O error occurs during parsing.
     * @throws JsonProcessingException if there's an error processing the JSON content.
     */
    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);
        Map<String, Object> result = new HashMap<>();
        
        node.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            
            if (value.isArray()) {
                ArrayNode arrayNode = (ArrayNode) value;
                if (arrayNode.size() > 0) {
                    result.put(key, arrayNode.get(0).asText());
                }
            } else {
                result.put(key, value.asText());
            }
        });
        
        return result;
    }
}
