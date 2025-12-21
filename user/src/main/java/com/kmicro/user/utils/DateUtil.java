package com.kmicro.user.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    private static final DateTimeFormatter HUMAN_READABLE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);

    private static final DateTimeFormatter HUMAN_READABLE_FORMAT_ZONE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z");

    public static String formatDateTimeHuman(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(HUMAN_READABLE_FORMAT);
    }

    public static String formatDateTimeHumanWithZone(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(HUMAN_READABLE_FORMAT);
    }

}
