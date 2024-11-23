package com.windsurf.common.core.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JsonUtils
 */
class JsonUtilsTest {

    static class TestObject {
        @JsonProperty
        private String stringField;
        @JsonProperty
        private int intField;
        @JsonProperty
        private LocalDateTime dateField;
        @JsonProperty
        private List<String> listField;

        public TestObject() {
        }

        public TestObject(String stringField, int intField, LocalDateTime dateField, List<String> listField) {
            this.stringField = stringField;
            this.intField = intField;
            this.dateField = dateField;
            this.listField = listField;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return intField == that.intField &&
                    stringField.equals(that.stringField) &&
                    dateField.equals(that.dateField) &&
                    listField.equals(that.listField);
        }
    }

    private final TestObject testObject = new TestObject(
            "test",
            42,
            LocalDateTime.of(2023, 1, 1, 12, 0),
            Arrays.asList("item1", "item2")
    );

    @Test
    void testToJson() {
        String json = JsonUtils.toJsonString(testObject);
        assertNotNull(json);
        assertTrue(json.contains("\"stringField\":\"test\""));
        assertTrue(json.contains("\"intField\":42"));
        assertTrue(json.contains("\"listField\":[\"item1\",\"item2\"]"));
    }

    @Test
    void testToJsonPretty() {
        String json = JsonUtils.toJsonString(testObject);
        assertNotNull(json);
        assertTrue(json.contains("\"stringField\":\"test\""));
        assertTrue(json.contains("\"intField\":42"));
        assertTrue(json.contains("\"listField\":[\"item1\",\"item2\"]"));
    }

    @Test
    void testParseObject() {
        String json = JsonUtils.toJsonString(testObject);
        TestObject parsed = JsonUtils.parseObject(json, TestObject.class);
        assertNotNull(parsed);
        assertEquals(testObject, parsed);
    }

    @Test
    void testParseList() {
        List<TestObject> list = Arrays.asList(testObject, testObject);
        String json = JsonUtils.toJsonString(list);
        List<TestObject> parsed = JsonUtils.parseArray(json, TestObject.class);
        assertNotNull(parsed);
        assertEquals(2, parsed.size());
        assertEquals(testObject, parsed.get(0));
        assertEquals(testObject, parsed.get(1));
    }

    @Test
    void testParseMap() {
        String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        Map<String, Object> map = JsonUtils.parseMap(json);
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    void testDeepCopy() {
        TestObject copy = JsonUtils.deepCopy(testObject, TestObject.class);
        assertNotNull(copy);
        assertEquals(testObject, copy);
        assertNotSame(testObject, copy);
    }

    @Test
    void testIsValidJson() {
        assertTrue(JsonUtils.isValidJson("{\"key\":\"value\"}"));
        assertTrue(JsonUtils.isValidJson("[1,2,3]"));
        assertFalse(JsonUtils.isValidJson("{invalid}"));
        assertFalse(JsonUtils.isValidJson("not json"));
    }

    @Test
    void testNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.toJsonString(null));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.parseObject(null, TestObject.class));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.parseObject("{}", null));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.parseArray(null, TestObject.class));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.parseArray("[]", null));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.parseMap(null));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.deepCopy(null, TestObject.class));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.deepCopy(testObject, null));
        assertThrows(IllegalArgumentException.class, () -> JsonUtils.isValidJson(null));
    }

    @Test
    void testInvalidJson() {
        assertThrows(RuntimeException.class, () -> JsonUtils.parseObject("invalid", TestObject.class));
        assertThrows(RuntimeException.class, () -> JsonUtils.parseArray("invalid", TestObject.class));
        assertThrows(RuntimeException.class, () -> JsonUtils.parseMap("invalid"));
    }

    @Test
    void testDateSerialization() {
        String json = JsonUtils.toJsonString(testObject);
        TestObject parsed = JsonUtils.parseObject(json, TestObject.class);
        assertEquals(testObject.dateField, parsed.dateField);
    }
}
