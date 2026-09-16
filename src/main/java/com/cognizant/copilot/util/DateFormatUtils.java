package com.cognizant.copilot.util;

import com.cognizant.copilot.config.AppConfig;

import java.time.format.DateTimeFormatter;

/**
 * Date format utilities.
 */
public class DateFormatUtils {

    public static final DateTimeFormatter INPUT_DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(AppConfig.getInstance().getString("date.input.format"));

    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(AppConfig.getInstance().getString("date.display.format"));

    /**
     * Get input date formatter
     */
    public static DateTimeFormatter getInputFormatter() {
        return INPUT_DATE_FORMATTER;
    }

    /**
     * Get display date formatter
     */
    public static DateTimeFormatter getDisplayFormatter() {
        return DISPLAY_DATE_FORMATTER;
    }
}
