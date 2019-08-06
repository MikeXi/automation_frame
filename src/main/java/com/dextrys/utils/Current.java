package com.dextrys.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.TimeZone;

import static com.dextrys.utils.Utils.f;

public class Current {
    private static final Logger logger = LoggerFactory.getLogger(Current.class);

    public static String date() {
        Calendar c = getCalendar();
        return f(2, c.get(Calendar.MONTH) + 1) + "/" + f(2, c.get(Calendar.DATE)) + "/" + c.get(Calendar.YEAR);
    }

    public static String year() {
        Calendar c = getCalendar();
        return String.valueOf(c.get(Calendar.YEAR));
    }

    public static String month() {
        Calendar c = getCalendar();
        return f(2, c.get(Calendar.MONTH) + 1);
    }

    public static String day() {
        Calendar c = getCalendar();
        return f(2, c.get(Calendar.DATE));
    }

    public static String time() {
        Calendar c = getCalendar();
        return f(2, c.get(Calendar.HOUR)) + ":" + f(2, c.get(Calendar.MINUTE));
    }

    public static String time(Integer minutesShift) {
        Calendar c = getCalendar();
        int day = c.get(Calendar.DATE);
        c.add(Calendar.MINUTE, minutesShift);
        int hourWShift = c.get(Calendar.HOUR);
        int minuteWShift = c.get(Calendar.MINUTE);
        if (c.get(Calendar.DATE) != day) {
            throw new RuntimeException("Unable to shift time for " + minutesShift);
        } else {
            return f(2, hourWShift) + ":" + f(2, minuteWShift);
        }
    }

    public static String hour() {
        Calendar c = getCalendar();
        return f(2, c.get(Calendar.HOUR));
    }

    public static String minute() {
        Calendar c = getCalendar();
        return f(2, c.get(Calendar.MINUTE));
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("PST"));
    }
}
