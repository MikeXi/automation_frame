package com.dextrys.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.allure.annotations.Step;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Ilia Vakhrushev
 * Date: 7/9/15
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static void waitABit(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Step("{0}")
    public static void report(String msg) {
        logger.info(msg);
    }

    public static void assertContainsAllAndEqual(Map<String, String> m1, Map<String, String> m2, String msg) {
        for (String key : m2.keySet()) {
            if (m1.containsKey(key)) {
                if (!m2.get(key).equals(m1.get(key))) {
                    Assert.fail(msg + ".\n" +
                            "Wrong values for the key '" + key + "': {" + m2.get(key) + "}, {" + m1.get(key) + "}");
                }
            } else {
                Assert.fail(msg + "\n" +
                        "Key '" + key + "' is missing.");
            }
        }
    }

    public static String getParameter(String url, String parameter) {
        try {
            List<NameValuePair> pairs = URLEncodedUtils.parse(new URI(url), "UTF-8");
            for (NameValuePair pair : pairs) {
                if (pair.getName().equals(parameter)) {
                    return pair.getValue();
                }
            }
            throw new RuntimeException("Parameter '" + parameter + "' is not present in the URL: " + url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Attachment(value = "{0}", type = "application/pdf")
    public static byte[] attachPDF(String name, byte[] bytes) {
        return bytes;
    }

    @Attachment(value = "{0}", type = "application/vnd.ms-excel")
    public static byte[] attachExcel(String name, byte[] bytes) {
        return bytes;
    }

    public static Map<String, String> map(String key, String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    }

    public static String buildName(String lastName, String firstName) {
        return lastName.toUpperCase() + ", " + String.valueOf(firstName.charAt(0)).toUpperCase() + firstName.substring(1).toLowerCase();
    }

    public static String f(int length, Object num) { // TODO just use .format
        String result = "";
        String value = String.valueOf(num);
        for (int i = 0; i < length - value.length(); i++) {
            result += "0";
        }
        result += value;
        return result;
    }

    /*
    To camel case
     */
    public static String toCC(String txt) {
        String result = "";
        boolean space = true;
        for (char ch : txt.toCharArray()) {
            result += space ? String.valueOf(ch).toUpperCase() : String.valueOf(ch).toLowerCase();
            space = ch == ' ';
        }
        return result;
    }

    public static String keepCharsOnly(String str) {
        String result = "";
        for (char ch : str.toCharArray()) {
            int i = ch;
            if ( (i >= 65 && i <=90) || (i >= 97 && i <= 122) || (i >= 48 && i <= 57) ) {
                result += ch;
            }
        }
        return result;
    }

    public static String getResourcePath(String filename) {
        return ClassLoader.getSystemResource(filename).getPath()
                .substring(1) // to remove first slash
                .replaceAll("/", "\\\\");
    }

    @Attachment("{0}")
    public static byte[] attachText(String name, String content) {
        return content.getBytes();
    }
    public static void solveCaptcha() {

     /*   OCR l = new OCR(0.70f);
        // "com/github/axet/lookup/fonts/font_1"
        l.loadFont(Utils.class, new File("fonts", "font_1"));
        String str = "";
        try {
            str = l.recognize(ImageIO.read(new File("C:\\Users\\Ilia Vakhrushev\\Downloads\\captcha.png")), "font_1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(str);*/


    }
}
