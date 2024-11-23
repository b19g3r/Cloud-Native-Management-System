package com.windsurf.common.core.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Text utility class extending Apache Commons Lang3 StringUtils
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * Empty string
     */
    private static final String NULLSTR = "";

    /**
     * Underscore
     */
    private static final char SEPARATOR = '_';

    /**
     * Get parameter value if not null
     *
     * @param value defaultValue value to check
     * @return value return value
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Check if a Collection is empty
     *
     * @param coll Collection to check
     * @return true: empty false: not empty
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * Check if an array is empty
     *
     * @param objects array to check
     * @return true: empty false: not empty
     */
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    /**
     * Convert camelCase to under_score
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean preCharIsUpperCase = true;
        boolean curCharIsUpperCase = true;
        boolean nextCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nextCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curCharIsUpperCase && !nextCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * Check if a string is contained in an array (case insensitive)
     *
     * @param str  string to check
     * @param strs array of strings
     * @return true if contained
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(org.apache.commons.lang3.StringUtils.trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Convert under_score to CamelCase
     * Example: HELLO_WORLD->HelloWorld
     *
     * @param name string to convert
     * @return converted string
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        if (name == null || name.isEmpty()) {
            return "";
        } else if (!name.contains("_")) {
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
        String[] camels = name.split("_");
        for (String camel : camels) {
            if (camel.isEmpty()) {
                continue;
            }
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * Convert under_score to camelCase
     * Example: user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Generate UUID without dashes
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate random string
     *
     * @param length length of string
     * @return random string
     */
    public static String randomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * Clean HTML tags
     */
    public static String cleanHtml(String html) {
        if (org.apache.commons.lang3.StringUtils.isBlank(html)) {
            return "";
        }
        return html.replaceAll("\\&[a-zA-Z]{1,10};", "")
                .replaceAll("<[^>]*>", "")
                .replaceAll("[(/>)<]", "");
    }

    /**
     * Escape HTML special characters
     */
    public static String escapeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    /**
     * Unescape HTML special characters
     */
    public static String unescapeHtml(String htmlEscaped) {
        return StringEscapeUtils.unescapeHtml4(htmlEscaped);
    }

    /**
     * Check if string is a mobile number
     */
    public static boolean isMobile(String str) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
            return false;
        }
        return Pattern.matches("^1[3-9]\\d{9}$", str);
    }

    /**
     * Check if string is an email address
     */
    public static boolean isEmail(String str) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
            return false;
        }
        return Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", str);
    }

    /**
     * Check if string is a URL
     */
    public static boolean isUrl(String str) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
            return false;
        }
        return Pattern.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", str);
    }
}