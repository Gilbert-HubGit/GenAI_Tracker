package com.cognizant.copilot.util;

/**
 * String utility methods.
 */
public class StringUtils {

    /**
     * Check if a string is blank (null or empty)
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Check if a string is not blank
     */
    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /**
     * Normalize a string for name matching (lowercase, remove punctuation, remove spaces)
     */
    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value
                .toLowerCase()
                .replace(",", "")
                .replace(".", "")
                .replaceAll("\\s+", "")
                .trim();
    }

    /**
     * Clean header value (remove line breaks, collapse whitespace)
     */
    public static String cleanHeader(String value) {
        return value
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Clean numeric ID (remove decimal point and everything after)
     */
    public static String cleanNumberId(String value) {
        if (isBlank(value)) {
            return "";
        }

        if (value.contains(".")) {
            return value.substring(0, value.indexOf("."));
        }

        return value.trim();
    }

    /**
     * Generate dummy email from name
     */
    public static String generateDummyEmail(String name) {
        if (isBlank(name)) {
            return "";
        }

        return name.toLowerCase()
                .replace(",", "")
                .replace(".", "")
                .replaceAll("\\s+", ".")
                + "@cognizant.com";
    }
}
