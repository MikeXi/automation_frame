package com.dextrys.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import ru.yandex.qatools.allure.annotations.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Ilia Vakhrushev
 * Date: 8/11/15
 */
public class Assert {

  //  @Step("{2}: \"{0}\"")
    public static void equals(String actual, String expected, String name) {
        org.testng.Assert.assertEquals(actual, expected, name + " is wrong");
    }

   // @Step("{2}: \"{0}\"")
    public static void equals(Integer actual, Integer expected, String message) {
        org.testng.Assert.assertEquals(actual, expected, message);
    }

  //  @Step("{2}: \"{0}\" ~ \"{1}\"")
    public static void contains(String str, String subStr, String name) {
        org.testng.Assert.assertTrue(str.contains(subStr), name + " is wrong. String: '" + str + "', substring: '" + subStr + "'");
    }

  //  @Step("{2}: {0} vs {1}")
    public static void compare(String actual, String expected, String name, ComparisonType type) {
        if (type.equals(ComparisonType.EXACTLY)) {
            org.testng.Assert.assertEquals(actual, expected, name + " is wrong");
        } else {
            String message = name + " is wrong: expected(" + type + ") [" + expected + "] but found [" + actual + "]";
            if (type.equals(ComparisonType.WITHOUT_SPACES)) {
                if (!actual.replaceAll(" ", "").equals(expected.replaceAll(" ", ""))) {
                    org.testng.Assert.fail(message);
                }
            } else if (type.equals(ComparisonType.CASE_INSENSITIVE)) {
                if (!actual.toLowerCase().equals(expected.toLowerCase())) {
                    org.testng.Assert.fail(message);
                }
            } else if (type.equals(ComparisonType.CONTAINS)) {
                if (!actual.contains(expected)) {
                    org.testng.Assert.fail(message);
                }
            } else if (type.equals(ComparisonType.PART_OF)) {
                if (!expected.contains(actual)) {
                    org.testng.Assert.fail(message);
                }
            } else if (type.equals(ComparisonType.NO_SPEC_CHARS)) {
                String a = Utils.keepCharsOnly(actual);
                String e = Utils.keepCharsOnly(expected);
                if (!a.equals(e)) {
                    org.testng.Assert.fail(message);
                }
            } else if (type.equals(ComparisonType.WITH_UNKNOWN)) {
                String fActual = normalizeUnknown(actual);
                String fExpected = normalizeUnknown(expected);
                if (!fActual.equals(fExpected)) {
                    org.testng.Assert.fail(message);
                }
            } else if (type.equals(ComparisonType.NULLABLE)) {
                String fActual = nullable(actual);
                String fExpected = nullable(expected);
                if (!fActual.equals(fExpected)) {
                    org.testng.Assert.fail(message);
                }
            } else {
                throw new RuntimeException("Comparison for type " + type + " is not implemented");
            }
        }
    }

    private static String nullable(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    private static String normalizeUnknown(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains("Unknown")) {
            return value.replaceAll("Unknown", "");
        }
        return value;
    }

    private static Map<String, String> flagsMap;
    static {
        flagsMap = new HashMap<String, String>(); // TODO where is the best place to store this map?
        flagsMap.put("The Joint Commission Sentinel Event", "sprite-tjcseFlag");
        flagsMap.put("Potentially Compensable Event (PCE)", "sprite-pceFlag");
        flagsMap.put("National Patient Safety Goal (NPSG) Non-Compliant", "sprite-npsgFlag");
        flagsMap.put("Quality Improvement (QI) Referral", "sprite-qireferralFlag");
        flagsMap.put("Peer Review Event", "sprite-peerreviewFlag");
        flagsMap.put("Risk Management Event", "sprite-riskmanagementFlag");
        flagsMap.put("Patient Grievance", "grievanceFlag.gif");
        flagsMap.put("NQF", "sprite-nqfFlag");
    }

    public static void verifyFlagPresent(WebElement summary, String flag) {
        if (!flagsMap.containsKey(flag)) {
            throw new RuntimeException("Unknown flag: " + flag);
        }
        String img = flagsMap.get(flag);
        String xpath = ".//*[@id='" + img + "' or contains(@src, '" + img + "')]";
        List<WebElement> flagElements = summary.findElements(By.xpath(xpath));
        if ( !(flagElements.size() > 0 && flagElements.get(0).isDisplayed()) ) {
            org.testng.Assert.fail("No flag '" + flag + "' was found in the summary");
        }
    }

    public static void containsElementWithText(List<WebElement> elements, String text, String message) {
        boolean found = false;
        for (WebElement el : elements) {
            if (el.getText().contains(text)) {
                found = true;
                break;
            }
        }
        if (!found) {
            org.testng.Assert.fail(message);
        }
    }

    public static void fail(String message) {
        org.testng.Assert.fail(message);
    }

    public static void present(By by, String failMessage, WebDriver driver) {
        try {
            driver.findElement(by);
        } catch (Exception e) {
            Assert.fail(failMessage);
        }
    }

    public static void xpath(String xpath, String successMsg, String failMsg, WebDriver driver, String... vars) {
        if (driver.findElements(By.xpath(String.format(xpath, vars))).size() > 0) {
            Utils.report(String.format(successMsg, vars));
        } else {
            fail(String.format(failMsg, vars));
        }
    }

    public static void optionText(WebElement option, String text) {
        equals(option.getText().trim(), text, "Selected option");
    }

    public static void lessThan(int i1, int i2, String errorMessage) {
        if (i1 >= i2) {
            fail(errorMessage);
        }
    }

    public static void greaterThan(int i1, int i2, String errorMessage) {
        if (i1 <= i2) {
            fail(errorMessage);
        }
    }
}
