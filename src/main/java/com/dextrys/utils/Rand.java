package com.dextrys.utils;

//import com.dextrys.config.Const;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebElement;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
//import java.util.TimeZone;

import static com.dextrys.utils.Utils.f;

public class Rand {

    private static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    private static final String GLOBAL_START = "1900-01-01 00:00:00";
    private static final String GLOBAL_END = "2015-01-01 00:00:00";
    private static Range lastDateRange;

    public static String uid() {
        return RandomStringUtils.randomAlphanumeric(4);
    }

    public static String uid(int n) {
        return RandomStringUtils.randomAlphanumeric(n);
    }

    public static String str(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static String element(Object[] array) {
        return (String) array[new Random().nextInt(array.length)];
    }

    public static WebElement element(List<WebElement> collection) {
        return collection.get(new Random().nextInt(collection.size()));
    }

    public static void resetLastDateRange() {
        lastDateRange = new Range(Timestamp.valueOf(GLOBAL_START).getTime(), Timestamp.valueOf(GLOBAL_END).getTime());
    }

    public static String dateBefore() {
        Range rightRange = new Range(Timestamp.valueOf(GLOBAL_START).getTime(), lastDateRange.getStart());
        Range range = new Range(rightRange.randPoint(), Timestamp.valueOf(GLOBAL_END).getTime());
        return format.format(range.randPoint());
    }

    public static String dateAfter() {
        Range rightRange = new Range(lastDateRange.getEnd(), Timestamp.valueOf(GLOBAL_END).getTime());
        Range range = new Range(rightRange.randPoint(), Timestamp.valueOf(GLOBAL_END).getTime());
        return format.format(range.randPoint());
    }

    public static String date() {
        Range range = new Range(Timestamp.valueOf(GLOBAL_START).getTime(), Timestamp.valueOf(GLOBAL_END).getTime());
        lastDateRange = range;
        return format.format(range.randPoint());
    }

    public static String tomorrowDate() {
        // Specify timezone to test with no drawbacks outside the office
        //Calendar c = Calendar.getInstance(TimeZone.getTimeZone("PDT"));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        return f(2, c.get(Calendar.MONTH) + 1) + "/" + f(2, c.get(Calendar.DATE)) + "/" + c.get(Calendar.YEAR);
    }

    public static String yesterdayDate() {
        // Specify timezone to test with no drawbacks outside the office
        //Calendar c = Calendar.getInstance(TimeZone.getTimeZone("PDT"));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        return f(2, c.get(Calendar.MONTH) + 1) + "/" + f(2, c.get(Calendar.DATE)) + "/" + c.get(Calendar.YEAR);
    }

    public static String time() {
        Random r = new Random();
        int minutes = r.nextInt(60);
        int hours = r.nextInt(24);
        return f(2, hours) + ":" + f(2, minutes);
    }


    public static Integer integer() {
        return new Random().nextInt(Integer.MAX_VALUE);
    }

    public static Integer integer(int min, int max) {
        return min + new Random().nextInt(max - min);
    }

    public static String phone() {
        return "(" + integer(100, 999) + ")" + integer(100, 999) + "-" + integer(1000, 9999);
    }

    private static class Range{
        long start;
        long end;

        private long getStart() {
            return start;
        }

        private long getEnd() {
            return end;
        }

        public Range(long p1, long p2) {
            if (p1 > p2) {
                start = p2;
                end = p1;
            } else {
                start = p1;
                end = p2;
            }
        }

        public long randPoint() {
            long diff = end - start + 1;
            long p = start + (long)(Math.random() * diff);
            return p;
        }
    }
}
