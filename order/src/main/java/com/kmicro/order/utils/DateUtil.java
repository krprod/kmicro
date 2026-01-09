package com.kmicro.order.utils;

import com.kmicro.order.constants.AppConstants;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    private static final DateTimeFormatter HUMAN_READABLE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SS a", Locale.ENGLISH);

    private static final DateTimeFormatter GENERIC_12HR_FORMAT =
            DateTimeFormatter.ofPattern(AppConstants.HR_12_FORMAT, Locale.ENGLISH);

    private static final DateTimeFormatter GENERIC_24HR_FORMAT =
            DateTimeFormatter.ofPattern(AppConstants.HR_24_FORMAT, Locale.ENGLISH);

    private static final DateTimeFormatter HUMAN_READABLE_FORMAT_ZONE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z");

    public static String humanFormat(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(HUMAN_READABLE_FORMAT);
    }

    public static String genricFormat(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(GENERIC_12HR_FORMAT);
    }

    public static String getTimeStampWithFormat(String format){
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of(AppConstants.ASIA_TIME_ZONE));
        if(format.equalsIgnoreCase("human")){
            return dateTime.format(HUMAN_READABLE_FORMAT);
        } else if (format.equalsIgnoreCase("12hrs")) {
            return dateTime.format(GENERIC_12HR_FORMAT);
        }
        return dateTime.format(GENERIC_24HR_FORMAT);
    }

    public static String getFormattedLDT(LocalDateTime dateTime, String format){
        if(format.equalsIgnoreCase("human")){
            return dateTime.format(HUMAN_READABLE_FORMAT);
        } else if (format.equalsIgnoreCase("12hrs")) {
            return dateTime.format(GENERIC_12HR_FORMAT);
        }
        return dateTime.format(GENERIC_24HR_FORMAT);
    }

    public static String formatDateTimeHumanWithZone(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(HUMAN_READABLE_FORMAT);
    }

    public static LocalDateTime convertInstantToLDT(Instant dateTimeUTC){
        return   dateTimeUTC
                .atZone(ZoneId.of(AppConstants.ASIA_TIME_ZONE))
                .toLocalDateTime();

    }

    public static Instant getInstantFromLDT(LocalDateTime dateTimeUTC){
        return dateTimeUTC.toInstant(ZoneOffset.UTC);
    }

}
