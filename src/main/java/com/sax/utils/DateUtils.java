package com.sax.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String parseString(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " - "
                + time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
