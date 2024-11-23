package com.windsurf.common.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TextUtils
 */
class StringUtilsTest {

    @Test
    void testNvl() {
        String value = "test";
        String defaultValue = "default";
        
        assertEquals(value, StringUtils.nvl(value, defaultValue));
        assertEquals(defaultValue, StringUtils.nvl(null, defaultValue));
    }

    @Test
    void testIsEmpty() {
        String[] emptyArray = new String[0];
        String[] nullArray = null;
        String[] nonEmptyArray = {"test"};

        assertTrue(StringUtils.isEmpty(emptyArray));
        assertTrue(StringUtils.isEmpty(nullArray));
        assertFalse(StringUtils.isEmpty(nonEmptyArray));
    }

    @Test
    void testToUnderScoreCase() {
        assertEquals("hello_world", StringUtils.toUnderScoreCase("helloWorld"));
        assertEquals("hello_world", StringUtils.toUnderScoreCase("HelloWorld"));
        assertEquals("hello_world_test", StringUtils.toUnderScoreCase("helloWorldTest"));
        assertNull(StringUtils.toUnderScoreCase(null));
        assertEquals("", StringUtils.toUnderScoreCase(""));
    }

    @Test
    void testInStringIgnoreCase() {
        String[] testArray = {"Test", "Hello", "World"};
        
        assertTrue(StringUtils.inStringIgnoreCase("test", testArray));
        assertTrue(StringUtils.inStringIgnoreCase("HELLO", testArray));
        assertFalse(StringUtils.inStringIgnoreCase("notfound", testArray));
        assertFalse(StringUtils.inStringIgnoreCase(null, testArray));
        assertFalse(StringUtils.inStringIgnoreCase("test", null));
    }

    @Test
    void testConvertToCamelCase() {
        assertEquals("HelloWorld", StringUtils.convertToCamelCase("HELLO_WORLD"));
        assertEquals("HelloWorld", StringUtils.convertToCamelCase("hello_world"));
        assertEquals("HelloWorldTest", StringUtils.convertToCamelCase("hello_world_test"));
        assertEquals("", StringUtils.convertToCamelCase(""));
        assertEquals("", StringUtils.convertToCamelCase(null));
    }

    @Test
    void testToCamelCase() {
        assertEquals("helloWorld", StringUtils.toCamelCase("hello_world"));
        assertEquals("helloWorldTest", StringUtils.toCamelCase("hello_world_test"));
        assertNull(StringUtils.toCamelCase(null));
        assertEquals("hello", StringUtils.toCamelCase("hello"));
    }

    @Test
    void testUuid() {
        String uuid = StringUtils.uuid();
        assertNotNull(uuid);
        assertEquals(32, uuid.length());
        assertFalse(uuid.contains("-"));
    }

    @Test
    void testRandomString() {
        int length = 10;
        String random = StringUtils.randomString(length);
        assertNotNull(random);
        assertEquals(length, random.length());
    }

    @Test
    void testCleanHtml() {
        String html = "<p>Hello <b>World</b></p>";
        String expected = "Hello World";
        assertEquals(expected, StringUtils.cleanHtml(html));
        assertEquals("", StringUtils.cleanHtml(""));
        assertEquals("", StringUtils.cleanHtml(null));
    }

    @Test
    void testEscapeHtml() {
        String text = "<p>Hello & World</p>";
        String escaped = StringUtils.escapeHtml(text);
        assertTrue(escaped.contains("&lt;"));
        assertTrue(escaped.contains("&gt;"));
        assertTrue(escaped.contains("&amp;"));
    }

    @Test
    void testUnescapeHtml() {
        String escaped = "&lt;p&gt;Hello &amp; World&lt;/p&gt;";
        String unescaped = StringUtils.unescapeHtml(escaped);
        assertEquals("<p>Hello & World</p>", unescaped);
    }
}
