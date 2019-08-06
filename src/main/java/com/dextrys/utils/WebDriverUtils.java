package com.dextrys.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.dextrys.config.Config;
import com.dextrys.config.Const;
import com.dextrys.DriverManager;
import com.dextrys.WebElementWrapper;
import com.dextrys.config.ScreenshotStrategy;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.screentaker.ViewportPastingStrategy;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

import static com.dextrys.utils.Utils.waitABit;

/**
 * User: Ilia Vakhrushev
 * Date: 6/18/15
 */
public class WebDriverUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverUtils.class);

    public static WebDriverWait getWait(WebDriver driver, long timeOutInSeconds, long sleepInMillis) {
        long timeout = (long) (timeOutInSeconds * Config.get().getTimeoutMultiplier());
        return new WebDriverWait(driver, timeout, sleepInMillis);
    }

    public static void makeScreenshot(WebDriver driver, String label) {
        if (Config.get().screenshotStrategy().equals(ScreenshotStrategy.ALL)) {
            makeScreenshotStep(driver, label);
        }
    }

    @Attachment("{1}")
    public static byte[] makeScreenshotStep(WebDriver driver, String label) {
        return doScreenshot((TakesScreenshot) driver);
    }

    @Attachment("{0}")
    public static byte[] makeFullScreenshotStep(String label) {
        return fullPageScreenshot();
    }

    public static void makeScreenshot(WebDriver driver) {
        if (Config.get().screenshotStrategy().equals(ScreenshotStrategy.ALL)) {
            makeScreenshotStep(driver);
        }
    }

    public static void makeRobotScreenshot() {
        if (Config.get().screenshotStrategy().equals(ScreenshotStrategy.ALL)) {
            makeRobotScreenshotStep();
        }
    }

    @Attachment("Screenshot")
    public static byte[] makeScreenshotStep(WebDriver driver) {
        return doScreenshot((TakesScreenshot) driver);
    }

    @Attachment("Screenshot")
    public static byte[] makeRobotScreenshotStep() {
        return doRobotScreenshot();
    }

    private static byte[] doScreenshot(TakesScreenshot driver) {
        byte[] bytes = new byte[0];
        try {
            long start = System.currentTimeMillis();
            bytes = driver.getScreenshotAs(OutputType.BYTES);
            long duration = System.currentTimeMillis() - start;
            logger.debug("makeScreenshot: " + duration + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static WebElement waitFor(final By by, WebDriver driver, long timeout, String message) {
        try {
            WebElement element = getWait(driver, timeout, Const.TIMEOUT_POLL).until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    try {
                        return driver.findElement(by);
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            return $(element);
        } catch (Exception e) {
            if (message == null) {
                throw new RuntimeException(e);
            } else {
                throw new RuntimeException(message, e);
            }
        }
    }

    public static WebElement waitFor(final By by, WebDriver driver) {
        return waitFor(by, driver, Const.TIMEOUT, null);
    }

    public static void waitForCount(final By by, final int num, WebDriver driver) {
        getWait(driver, Const.TIMEOUT, Const.TIMEOUT_POLL).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                try {
                    List<WebElement> elements = driver.findElements(by);
                    if (elements.size() == num) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    public static WebElement waitForTextChanged(final By by, final String text, WebDriver driver) {
        try {
            return getWait(driver, Const.TIMEOUT, Const.TIMEOUT_POLL).until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    try {
                        WebElement element = driver.findElement(by);
                        String txt = element.getText();
                        logger.debug(txt + " vs " + text);
                        if (!txt.equals(text)) {
                            return element;
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
        } catch (TimeoutException e) {
            throw new RuntimeException("Text is still '" + text + "'");
        }
    }

    public static String waitForSomeText(final By by, WebDriver driver) {
        return getWait(driver, Const.SHORT_TIMEOUT, Const.TIMEOUT_POLL).until(new Function<WebDriver, String>() {
            public String apply(WebDriver driver) {
                try {
                    WebElement element = driver.findElement(by);
                    String txt = element.getText();
                    if (!txt.trim().equals("")) {
                        return txt;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }

    public static WebElement waitForVisible(final By by, WebDriver driver) {
        return waitForVisible(by, Const.TIMEOUT, driver);
    }

    public static WebElement waitForVisibleShort(final By by, WebDriver driver) {
        return waitForVisible(by, Const.SHORT_TIMEOUT, driver);
    }

    private static WebElement waitForVisible(final By by, long timeout, WebDriver driver) {
        try { // for debug purposes
            WebElement element = getWait(driver, timeout, Const.TIMEOUT_POLL).until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    try {
                        WebElement element = driver.findElement(by);
                        if (element.isDisplayed()) {
                            return element;
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            return $(element);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WebElement waitForVisible(final WebElement parent, final By by, WebDriver driver) {
        WebElement element = getWait(driver, Const.TIMEOUT, Const.TIMEOUT_POLL).until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                try {
                    WebElement element = parent.findElement(by);
                    if (element.isDisplayed()) {
                        return element;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        });
        return $(element);
    }

    public static void waitForNotVisible(final By by, WebDriver driver) {
        waitForNotVisible(by, driver, Const.TIMEOUT);
    }

    public static void waitForNotVisibleShort(final By by, WebDriver driver) {
        waitForNotVisible(by, driver, Const.SHORT_TIMEOUT);
    }

    public static void waitForNotVisible(final By by, WebDriver driver, long timeout) {
        getWait(driver, timeout, Const.TIMEOUT_POLL).until(new Predicate<WebDriver>() {
            public boolean apply(org.openqa.selenium.WebDriver driver) {
                try {
                    WebElement element = driver.findElement(by);
                    if (element.isDisplayed()) {
                        return false;
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    return true;
                }
            }
        });
    }

    public static void waitForNotPresent(final By by, WebDriver driver) {
        getWait(driver, Const.TIMEOUT, Const.TIMEOUT_POLL).until(new Predicate<WebDriver>() {
            public boolean apply(org.openqa.selenium.WebDriver driver) {
                try {
                    driver.findElement(by);
                    return false;
                } catch (Exception e) {
                    return true;
                }
            }
        });
    }

    public static WebElement waitFor(final WebElement parent, final By by, WebDriver driver, long timeout) {
        WebElement element = null;
        try {
            element = getWait(driver, timeout, Const.TIMEOUT_POLL).until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    try {
                        return parent.findElement(by);
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
        } catch (TimeoutException e) {
            throw new RuntimeException(timeout + "sec timeout exceeded:\nlocator:" + by + "\nparent: " + parent);
        }
        return $(element);
    }

    public static WebElement waitFor(final WebElement parent, final By by, WebDriver driver) {
        return waitFor(parent, by, driver, Const.TIMEOUT);
    }

    public static WebElement waitForShort(final By by, WebDriver driver) {
        return waitFor(by, driver, Const.SHORT_TIMEOUT, null);
    }

    public static WebElement getBtnWithText(String txt, WebDriver driver) {
        return waitFor(By.xpath("//a[contains(@class, 'x-btn')][.//span[text()='" + txt + "']]"), driver);
    }

    public static WebElement getBtnWithText(WebElement parent, String txt, WebDriver driver) {
        return waitFor(parent, By.xpath(".//a[contains(@class, 'x-btn')][.//span[text()='" + txt + "']]"), driver);
    }

    public static void clickWithJS(final WebElement element, WebDriver driver) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
        checkJSErrors(driver);
    }

    public static void scrollTo(WebElement element, WebDriver driver) {
        if (Config.get().isScrollingEnabled()) {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].scrollIntoView(false);", element);
        }
    }

    public static void blurActiveElement(WebDriver driver) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("document.activeElement.blur();");
    }

    public static void waitNotVisible(final WebElement element, WebDriver driver) {
        getWait(driver, Const.TIMEOUT, Const.TIMEOUT_POLL).until(new Predicate<WebDriver>() {
            public boolean apply(org.openqa.selenium.WebDriver driver) {
                try {
                    return !element.isDisplayed();
                } catch (Exception e) {
                    return true;
                }
            }
        });
    }

    public static void waitNotVisibleShort(final WebElement element, WebDriver driver) {
        getWait(driver, Const.SHORT_TIMEOUT, Const.TIMEOUT_POLL).until(new Predicate<WebDriver>() {
            public boolean apply(org.openqa.selenium.WebDriver driver) {
                try {
                    return !element.isDisplayed();
                } catch (Exception e) {
                    return true;
                }
            }
        });
    }

    public static void checkJSErrors(WebDriver driver) {
        if (Config.get().checkJS()) {
            driver.getCurrentUrl(); // we need to make any request to the driver to be sure that all latest logs are grabbed
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            List<LogEntry> entries = new ArrayList<LogEntry>();
            for (LogEntry entry : logEntries) {
                if (entry.getLevel().equals(Level.SEVERE)) {
                    entries.add(entry);
                }
            }
            if (entries.size() > 0) {
                String message = "JS errors: " + entries.size() + "\n\n";
                for (LogEntry entry : entries) {
                    message += entry.getLevel() + " " + entry.getMessage() + "\n\n";
                }
                throw new RuntimeException(message);
            }
        }
    }

    public static byte[] fullPageScreenshot() {
        long start = System.currentTimeMillis();
        byte[] imageBytes = new byte[0];
        try {
            Screenshot screenshot = null;
            if (Config.get().IE()) { // TODO as I remember we should specify shooting strategy only for chrome - check and change condition
                screenshot = new AShot().takeScreenshot(DriverManager.get());
            } else {
                screenshot = new AShot()
                        .shootingStrategy(new ViewportPastingStrategy(0))// TODO try to implement new strategy with correct handling floating caption on the report a safety event form
                        .takeScreenshot(DriverManager.get());
            }
 
            ByteArrayOutputStream baos = new ByteArrayOutputStream();         
            try {
                ImageIO.write(screenshot.getImage(), "png", baos);
            } catch (IOException e) {
                System.out.println("Cannot write screenshot to ByteArrayOutputStream"); // TODO logging
            }
             imageBytes = baos.toByteArray();

            if (imageBytes.length == 0) {
                String errorMessage = "Converted byte array for screenshot is empty.";
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            System.out.println("Unable to take screenshot"); // TODO logging
        }
        long duration = System.currentTimeMillis() - start;
        logger.debug("makeScreenshot: " + duration + "ms");
        return imageBytes;
    }

    @Deprecated // FIXME Wrong behavior on pages with frames
    @Attachment("{1}")
    public static byte[] elementScreenshot(WebElement element, String message) {
        waitABit(3000);
        byte[] imageBytes = new byte[0];
        Screenshot screenshot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(DriverManager.get(), element);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(screenshot.getImage(), "png", baos);
        } catch (IOException e) {
            System.out.println("Cannot write screenshot to ByteArrayOutputStream"); // TODO logging
        }
        imageBytes = baos.toByteArray();

        if (imageBytes.length == 0) {
            String errorMessage = "Converted byte array for screenshot is empty.";
            throw new RuntimeException(errorMessage);
        }
        return imageBytes;
    }

    /**
     * Try to click n times if StaleElementReferenceException was risen.
     * @param parent
     * @param by
     * @param driver
     * @param n
     * @return
     */
    public static WebElement tryToClick(WebElement parent, By by, WebDriver driver, int n) {
        boolean done = false;
        WebElement element = null;
        for (int i = 0; i < n; i++ ) {
            try {
                WebElement tmp = waitFor(parent, by, driver);
                tmp.click();
                element = tmp;
                done = true;
                break;
            } catch (StaleElementReferenceException e) {
                waitABit(500);
            }
        }
        if (!done) {
            throw new RuntimeException("Unable to click element within " + n + " tries");
        }
        return element;
    }

    public static WebElement $(WebElement element) {
        return new WebElementWrapper(element);
    }

    public static Object executeJS(String code, WebDriver driver) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        return executor.executeScript(code);
    }

    public static Object executeJS(String code, WebElement el, WebDriver driver) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        WebElement element = null;
        if (el instanceof WebElementWrapper) {
            element = ((WebElementWrapper) el).getWrappedElement();
        } else {
            element = el;
        }
        return executor.executeScript(code, element);
    }

    public static void processCertWarning(WebDriver driver) {
        if (Config.get().IE() && Config.get().withProxy()) {
            waitABit(1000);
            boolean warningPresent = Boolean.parseBoolean(String.valueOf(WebDriverUtils.executeJS("if (document.getElementById('overridelink')) return true; else return false;", driver)));
            if (warningPresent) {
                logger.info("Closing certificate warning");
                driver.get("javascript:document.getElementById('overridelink').click();");
                logger.info("Certificate warning is closed");
            } else {
                logger.info("Certificate warning is not present");
            }
        }
    }

    public static String JSESSIONID(String label) {
        String id = null;
        try {
            id = DriverManager.get().manage().getCookieNamed("JSESSIONID").getValue();
        } catch (Exception e) { }
        logger.debug(">>>" + label + ": " + id);
        return id;
    }

    /**
     * Method for correct handling SRM dropdown control
     * We can specify ignore value for random values - e.g. Unknown for injuries
     * @param value
     * @param driver
     * @param container
     * @return
     */
    public static String selectDropdown(String value, WebDriver driver, WebElement container, Object... args) {
        WebElement arrow = waitFor(container, By.xpath(".//div[contains(@class, 'x-form-arrow-trigger')]"), driver);
        WebDriverUtils.scrollTo(arrow, driver);
        arrow.click();
        By listXPath = By.xpath("//div[contains(@class, 'x-boundlist-default')]" +
                "[not(contains(@style, 'display: none')) and not(contains(@style, 'DISPLAY: none'))]");    // second for IE
        waitForVisible(listXPath, driver);
        String valueToSelect = null;
        if (value.equals("RANDOM")) {
            Set<String> ignoreValues = new HashSet<String>();
            int randTryNum = 3;
            if (args[0] != null) {
                ignoreValues = (Set<String>) args[0];
            }
            List<WebElement> options = driver.findElement(listXPath).findElements(By.tagName("li"));
            do {
                int idx = new Random().nextInt(options.size());
                WebElement option = options.get(idx);
                valueToSelect = option.getText();
                if (randTryNum-- == 0) {throw new RuntimeException("Unable to choose random value after " + randTryNum + " tries");};
            } while (ignoreValues.contains(valueToSelect));
            Utils.report(valueToSelect);
        } else {
            valueToSelect = value;
        }
        logger.debug("    valueToSelect: " + valueToSelect);
        WebElement item = waitFor(By.xpath("//li[text()=" + Quotes.escape(valueToSelect) + "]"), driver);
        clickWithJS(item, driver); // some items could be considered as not visible by WebDriver. E.g. Unknown Gender for Patient
        blurActiveElement(driver);
        waitForNotVisibleShort(By.xpath("//li[text()=" + Quotes.escape(valueToSelect) + "]"), driver);
        //waitForAttributeChanged(By.xpath("//li[text()=" + Quotes.escape(valueToSelect) + "]"), "class", "x-boundlist-item", driver);
        try {
            logger.debug("input: " + container.findElement(By.xpath(".//input")).getAttribute("value"));
        } catch (Exception e) {
            // do nothing
        }
        return valueToSelect;
    }

    public static WebElement waitForInFrame(final By by, final String frame, WebDriver driver) {
        return getWait(driver, Const.TIMEOUT, Const.TIMEOUT_POLL).until(new Function<WebDriver, WebElement>() {
            @Nullable
            public WebElement apply(WebDriver driver) {
                try {
                    try {
                        driver.switchTo().frame(frame);
                    } catch (Exception e) {}
                    WebElement el = null;
                    try {
                        el = driver.findElement(by);
                    } catch (Exception e) {
                        driver.switchTo().parentFrame();
                    }
                    return el;
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }

    public static void fireMouseEvent(WebElement el, String event, WebDriver driver) {
        String code = "if (document.createEvent) {\n" +
                "var simulateEvent = document.createEvent('MouseEvents');\n" +
                "simulateEvent.initMouseEvent('" + event + "',true,true,document.defaultView,0,0,0,0,0,false,false,false,0,null,null);\n" +
                "arguments[0].dispatchEvent(simulateEvent);\n" +
                "} else {\n" +
                "var evObj = document.createEventObject();\n" +
                "arguments[0].fireEvent('on" + event + "', evObj);\n" +
                "}";
        executeJS(code, el, driver);
    }

    public static void fireHTMLEvent(WebElement el, String event, WebDriver driver) {
        String code = "if (document.createEvent) {\n" +
                "var simulateEvent = document.createEvent('HTMLEvents');\n" +
                "simulateEvent.initEvent('" + event + "',true,true,document.defaultView,0,0,0,0,0,false,false,false,0,null,null);\n" +
                "arguments[0].dispatchEvent(simulateEvent);\n" +
                "} else {\n" +
                "var evObj = document.createEventObject();\n" +
                "arguments[0].fireEvent('on" + event + "', evObj);\n" +
                "}";
        executeJS(code, el, driver);
    }

    public static void fireChangeEvent(WebElement el, String data, WebDriver driver) {
        String code = "if (document.createEvent) {\n" +
                "var simulateEvent = document.createEvent('HTMLEvents');\n" +
                "simulateEvent.initEvent('change',true,true,document.defaultView,0,0,0,0,0,false,false,false,0,null,null);\n" +
                "arguments[0].dispatchEvent(simulateEvent);\n" +
                "} else {\n" +
                "var evObj = document.createEventObject();\n" +
                "evObj = '" + data + "';\n" +
                "arguments[0].fireEvent('onchange', evObj);\n" +
                "}";
        executeJS(code, el, driver);
    }

    public static String waitForAttributeChanged(final By by, final String attr, final String value, WebDriver driver) {
        try {
            return getWait(driver, Const.TIMEOUT, Const.TIMEOUT_POLL).until(new Function<WebDriver, String>() {
                public String apply(WebDriver driver) {
                    try {
                        WebElement element = driver.findElement(by);
                        String cValue = element.getAttribute(attr);
                        logger.debug(cValue + " vs " + value);
                        if (!cValue.equals(value)) {
                            return cValue;
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
        } catch (TimeoutException e) {
            throw new RuntimeException("Attribute is not changed: " + attr + " = " + value + "");
        }
    }

    public static void acceptAllConfirmations(WebDriver driver) {
        WebDriverUtils.executeJS("window.confirm = function () {\n" +
                "\tconsole.log('replacing confirm() with positive function');\n" +
                "\treturn true;\n" +
                "};", driver);
    }

    public static byte[] doRobotScreenshot() {
        byte[] bytes = new byte[0];
        try {
            java.awt.Rectangle screenRect = new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(capture, "png", baos);
            baos.flush();
            bytes = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            logger.error("Unable to get screenshot with Robot", e);
        }
        return bytes;
    }
}
