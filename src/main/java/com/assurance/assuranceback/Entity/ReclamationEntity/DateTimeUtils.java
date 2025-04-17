package com.assurance.assuranceback.Entity.ReclamationEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    private static final LocalDateTime CURRENT_DATE_TIME = LocalDateTime.parse("2025-04-16T20:34:27");
    private static final String CURRENT_USER = "Hosinusss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime getCurrentDateTime() {
        return CURRENT_DATE_TIME;
    }

    public static String getCurrentUser() {
        return CURRENT_USER;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    public static String getCurrentFormattedDateTime() {
        return formatDateTime(CURRENT_DATE_TIME);
    }
}