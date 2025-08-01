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
 * Deserializador personalizado para manejar los identificadores que pueden ser arrays o strings
 */
public class IdentifierDeserializer extends JsonDeserializer<Map<String, Object>> {

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
