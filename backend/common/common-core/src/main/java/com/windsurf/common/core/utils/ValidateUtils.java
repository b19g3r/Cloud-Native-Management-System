package com.windsurf.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Validation utility class
 */
@Slf4j
public class ValidateUtils {

    /**
     * Regex pattern: simple mobile number
     */
    private static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";

    /**
     * Regex pattern: exact mobile number
     * Mobile: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 178, 182, 183, 184, 187, 188, 198
     * Unicom: 130, 131, 132, 145, 155, 156, 166, 171, 175, 176, 185, 186
     * Telecom: 133, 149, 153, 173, 177, 180, 181, 189, 199
     * Virtual: 170
     */
    private static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(16[6])|(17[0-3,5-8])|(18[0-9])|(19[8,9]))\\d{8}$";

    /**
     * Regex pattern: telephone number
     */
    private static final String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}$";

    /**
     * Regex pattern: email
     */
    private static final String REGEX_EMAIL = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * Regex pattern: URL
     */
    private static final String REGEX_URL = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";

    /**
     * Regex pattern: MAC address
     */
    private static final String REGEX_MAC = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

    /**
     * Regex pattern: Chinese characters
     */
    private static final String REGEX_CHINESE = "^[\\u4e00-\\u9fa5]+$";

    /**
     * Regex pattern: passport
     */
    private static final String REGEX_PASSPORT = "^[a-zA-Z]\\d{8}$";

    /**
     * Regex pattern: postal code
     */
    private static final String REGEX_POSTCODE = "^[1-9]\\d{5}$";

    /**
     * Regex pattern: IP address
     */
    private static final String REGEX_IP = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

    /**
     * Validate simple mobile number
     */
    public static boolean isMobileSimple(String mobile) {
        return Pattern.matches(REGEX_MOBILE_SIMPLE, mobile);
    }

    /**
     * Validate exact mobile number
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE_EXACT, mobile);
    }

    /**
     * Validate telephone number
     */
    public static boolean isTel(String tel) {
        return Pattern.matches(REGEX_TEL, tel);
    }

    /**
     * Check if the string is a valid email address
     */
    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Validate URL
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * Validate MAC address
     */
    public static boolean isMac(String mac) {
        return Pattern.matches(REGEX_MAC, mac);
    }

    /**
     * Validate Chinese characters
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * Validate passport number
     */
    public static boolean isPassport(String passport) {
        if (passport == null || passport.isEmpty()) {
            return false;
        }
        // 护照号码格式：1 个字母 + 8 个数字，例如：G12345678
        return Pattern.matches(REGEX_PASSPORT, passport);
    }

    /**
     * Validate postal code
     */
    public static boolean isPostcode(String postcode) {
        return Pattern.matches(REGEX_POSTCODE, postcode);
    }

    /**
     * Validate IP address
     */
    public static boolean isIp(String ip) {
        return Pattern.matches(REGEX_IP, ip);
    }

    /**
     * Validate string length range
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate number range
     */
    public static boolean isNumberInRange(Number number, Number min, Number max) {
        if (number == null || min == null || max == null) {
            return false;
        }
        double value = number.doubleValue();
        return value >= min.doubleValue() && value <= max.doubleValue();
    }

    /**
     * Validate string is number
     */
    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate string is integer
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate string is positive integer
     */
    public static boolean isPositiveInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            return Integer.parseInt(str) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate longitude
     */
    public static boolean isLongitude(String longitude) {
        try {
            double lon = Double.parseDouble(longitude);
            return lon >= -180 && lon <= 180;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate latitude
     */
    public static boolean isLatitude(String latitude) {
        try {
            double lat = Double.parseDouble(latitude);
            return lat >= -90 && lat <= 90;
        } catch (Exception e) {
            return false;
        }
    }
}