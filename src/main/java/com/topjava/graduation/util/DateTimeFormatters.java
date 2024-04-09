package com.topjava.graduation.util;

import lombok.experimental.UtilityClass;
import org.springframework.format.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class DateTimeFormatters {

    public static class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

        @Override
        public LocalDateTime parse(String text, Locale locale) {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        @Override
        public String print(LocalDateTime ldt, Locale locale) {
            return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}