package com.windsurf.common.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ValidateUtils
 */
class ValidateUtilsTest {

    @Test
    void testIsMobileSimple() {
        assertTrue(ValidateUtils.isMobileSimple("13800138000"));
        assertFalse(ValidateUtils.isMobileSimple("1380013800")); // too short
        assertFalse(ValidateUtils.isMobileSimple("138001380000")); // too long
        assertFalse(ValidateUtils.isMobileSimple("23800138000")); // wrong prefix
        assertFalse(ValidateUtils.isMobileSimple("abcdefghijk")); // not a number
    }

    @Test
    void testIsMobile() {
        assertTrue(ValidateUtils.isMobile("13800138000")); // China Mobile
        assertTrue(ValidateUtils.isMobile("13300138000")); // China Telecom
        assertTrue(ValidateUtils.isMobile("13100138000")); // China Unicom
        assertFalse(ValidateUtils.isMobile("12345678901")); // Invalid prefix
        assertFalse(ValidateUtils.isMobile("1234567890")); // Too short
        assertFalse(ValidateUtils.isMobile("123456789012")); // Too long
    }

    @Test
    void testIsTel() {
        assertTrue(ValidateUtils.isTel("010-12345678"));
        assertTrue(ValidateUtils.isTel("0755-1234567"));
        assertTrue(ValidateUtils.isTel("02112345678"));
        assertFalse(ValidateUtils.isTel("1234567")); // Too short
        assertFalse(ValidateUtils.isTel("123-45678901")); // Invalid format
    }

    @Test
    void testIsEmail() {
        assertTrue(ValidateUtils.isEmail("test@example.com"));
        assertTrue(ValidateUtils.isEmail("test.name@example.com"));
        assertTrue(ValidateUtils.isEmail("test@sub.example.com"));
        assertFalse(ValidateUtils.isEmail("test@.com")); // Invalid domain
        assertFalse(ValidateUtils.isEmail("test@com")); // Missing dot
        assertFalse(ValidateUtils.isEmail("test.example.com")); // Missing @
    }

    @Test
    void testIsUrl() {
        assertTrue(ValidateUtils.isUrl("http://example.com"));
        assertTrue(ValidateUtils.isUrl("https://example.com"));
        assertTrue(ValidateUtils.isUrl("ftp://example.com"));
        assertFalse(ValidateUtils.isUrl("example.com")); // Missing protocol
        assertFalse(ValidateUtils.isUrl("http:/example.com")); // Invalid format
    }

    @Test
    void testIsMac() {
        assertTrue(ValidateUtils.isMac("00:11:22:33:44:55"));
        assertTrue(ValidateUtils.isMac("00-11-22-33-44-55"));
        assertFalse(ValidateUtils.isMac("00:11:22:33:44")); // Too short
        assertFalse(ValidateUtils.isMac("00:11:22:33:44:55:66")); // Too long
        assertFalse(ValidateUtils.isMac("00:11:22:33:44:GG")); // Invalid characters
    }

    @Test
    void testIsChinese() {
        assertTrue(ValidateUtils.isChinese("中文"));
        assertTrue(ValidateUtils.isChinese("你好世界"));
        assertFalse(ValidateUtils.isChinese("Hello")); // English
        assertFalse(ValidateUtils.isChinese("中文123")); // Mixed characters
        assertFalse(ValidateUtils.isChinese("")); // Empty string
    }

    @Test
    void testIsPassport() {
        assertTrue(ValidateUtils.isPassport("A12345678"));
        assertFalse(ValidateUtils.isPassport("12345")); // Too short
        assertFalse(ValidateUtils.isPassport("ABCDEFGHIJKLMNOPQRS")); // Too long
        assertFalse(ValidateUtils.isPassport("123456")); // No letters
    }

    @Test
    void testIsPostcode() {
        assertTrue(ValidateUtils.isPostcode("100000"));
        assertTrue(ValidateUtils.isPostcode("518000"));
        assertFalse(ValidateUtils.isPostcode("1000000")); // Too long
        assertFalse(ValidateUtils.isPostcode("10000")); // Too short
        assertFalse(ValidateUtils.isPostcode("00000")); // Invalid format
    }

    @Test
    void testIsIp() {
        assertTrue(ValidateUtils.isIp("192.168.1.1"));
        assertTrue(ValidateUtils.isIp("255.255.255.255"));
        assertTrue(ValidateUtils.isIp("0.0.0.0"));
        assertFalse(ValidateUtils.isIp("256.1.2.3")); // Invalid number
        assertFalse(ValidateUtils.isIp("1.2.3")); // Too few octets
        assertFalse(ValidateUtils.isIp("1.2.3.4.5")); // Too many octets
    }

    @Test
    void testIsLengthInRange() {
        assertTrue(ValidateUtils.isLengthInRange("test", 1, 10));
        assertTrue(ValidateUtils.isLengthInRange("test", 4, 4));
        assertFalse(ValidateUtils.isLengthInRange("test", 5, 10)); // Too short
        assertFalse(ValidateUtils.isLengthInRange("test", 1, 3)); // Too long
        assertFalse(ValidateUtils.isLengthInRange(null, 1, 10)); // Null string
    }

    @Test
    void testIsNumberInRange() {
        assertTrue(ValidateUtils.isNumberInRange(5, 1, 10));
        assertTrue(ValidateUtils.isNumberInRange(1.5, 1.0, 2.0));
        assertFalse(ValidateUtils.isNumberInRange(0, 1, 10)); // Too small
        assertFalse(ValidateUtils.isNumberInRange(11, 1, 10)); // Too large
        assertFalse(ValidateUtils.isNumberInRange(null, 1, 10)); // Null number
    }

    @Test
    void testIsNumber() {
        assertTrue(ValidateUtils.isNumber("123"));
        assertTrue(ValidateUtils.isNumber("123.456"));
        assertTrue(ValidateUtils.isNumber("-123.456"));
        assertFalse(ValidateUtils.isNumber("abc")); // Not a number
        assertFalse(ValidateUtils.isNumber("")); // Empty string
        assertFalse(ValidateUtils.isNumber(null)); // Null string
    }

    @Test
    void testIsInteger() {
        assertTrue(ValidateUtils.isInteger("123"));
        assertTrue(ValidateUtils.isInteger("-123"));
        assertFalse(ValidateUtils.isInteger("123.456")); // Decimal
        assertFalse(ValidateUtils.isInteger("abc")); // Not a number
        assertFalse(ValidateUtils.isInteger("")); // Empty string
        assertFalse(ValidateUtils.isInteger(null)); // Null string
    }

    @Test
    void testIsPositiveInteger() {
        assertTrue(ValidateUtils.isPositiveInteger("123"));
        assertFalse(ValidateUtils.isPositiveInteger("0")); // Zero
        assertFalse(ValidateUtils.isPositiveInteger("-123")); // Negative
        assertFalse(ValidateUtils.isPositiveInteger("123.456")); // Decimal
        assertFalse(ValidateUtils.isPositiveInteger("")); // Empty string
        assertFalse(ValidateUtils.isPositiveInteger(null)); // Null string
    }

    @Test
    void testIsLongitude() {
        assertTrue(ValidateUtils.isLongitude("0"));
        assertTrue(ValidateUtils.isLongitude("180"));
        assertTrue(ValidateUtils.isLongitude("-180"));
        assertFalse(ValidateUtils.isLongitude("181")); // Too large
        assertFalse(ValidateUtils.isLongitude("-181")); // Too small
        assertFalse(ValidateUtils.isLongitude("abc")); // Not a number
    }

    @Test
    void testIsLatitude() {
        assertTrue(ValidateUtils.isLatitude("0"));
        assertTrue(ValidateUtils.isLatitude("90"));
        assertTrue(ValidateUtils.isLatitude("-90"));
        assertFalse(ValidateUtils.isLatitude("91")); // Too large
        assertFalse(ValidateUtils.isLatitude("-91")); // Too small
        assertFalse(ValidateUtils.isLatitude("abc")); // Not a number
    }
}
