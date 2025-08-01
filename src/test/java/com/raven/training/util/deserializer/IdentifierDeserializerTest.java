package com.raven.training.util.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new IdentifierDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void deserialize_WithSimpleKeyValue_ReturnsMapWithStringValue() throws JsonProcessingException {
        String json = "{\"key\": \"value\"}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals(1, result.size(), "El mapa debe contener un elemento");
        assertEquals("value", result.get("key"), "El valor debe ser 'value'");
    }

    @Test
    void deserialize_WithArrayValue_ReturnsMapWithFirstArrayElement() throws JsonProcessingException {
        String json = "{\"key\": [\"first\", \"second\"]}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals(1, result.size(), "El mapa debe contener un elemento");
        assertEquals("first", result.get("key"), "Debe tomar el primer elemento del array");
    }

    @Test
    void deserialize_WithEmptyArray_ReturnsEmptyMap() throws JsonProcessingException {
        String json = "{\"key\": []}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertTrue(result.isEmpty(), "El mapa debe estar vacío para arrays vacíos");
    }

    @Test
    void deserialize_WithMultipleKeys_ReturnsMapWithAllKeys() throws JsonProcessingException {
        String json = "{\"key1\": \"value1\", \"key2\": [\"arrayValue\"], \"key3\": 123}";

        Map<String, Object> result = objectMapper.readValue(json, Map.class);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals(3, result.size(), "El mapa debe contener 3 elementos");
        assertEquals("value1", result.get("key1"), "key1 debe tener el valor 'value1'");
        assertEquals("arrayValue", result.get("key2"), "key2 debe tener el valor 'arrayValue'");
        assertEquals("123", result.get("key3"), "key3 debe tener el valor '123' convertido a string");
    }
}
