package com.dextrys.steps;

//import com.google.common.base.Predicate;
import com.dextrys.DriverManager;
import com.dextrys.config.Config;
import com.dextrys.config.Const;
import com.dextrys.utils.Utils;
import com.dextrys.utils.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
//import java.util.Set;

import static com.dextrys.utils.WebDriverUtils.$;
//import static com.dextrys.utils.WebDriverUtils.clickWithJS;

public class WebDriverSteps {

    public static final String _MAIN_FRAME = "frmMain";
    public static final String _MENU_FRAME = "frmMenu";
    protected WebDriver driver;

    protected WebDriverWait getWait(long timeout) {
        return WebDriverUtils.getWait(driver, timeout, Const.TIMEOUT_POLL);
    }

    public WebDriverSteps(WebDriver driver) {
        this.driver = driver;
    }

    protected WebElement find(By by) {
        return $(driver.findElement(by));
    }

    protected List<WebElement> findAll(By by) {
        List<WebElement> elements = driver.findElements(by);
        List<WebElement> wrappedElements = new ArrayList<WebElement>();
        for (WebElement element : elements) {
            wrappedElements.add($(element));
        }
        return wrappedElements;
    }

    public void _switchToMainFrame() { // TODO store current frame to prevent reccuring attempt (how to deal with parallel execution?)
        driver.switchTo().defaultContent();
        waitFor(By.name(_MAIN_FRAME));
        driver.switchTo().frame(_MAIN_FRAME);
    }

    public void _switchToMenuFrame() {
        driver.switchTo().defaultContent();
        waitFor(By.name(_MENU_FRAME));
        driver.switchTo().frame(_MENU_FRAME);
    }

    protected void switchToOpenerWindow() {
        // for situations when window closes with delay, and we are trying to switch to it instead of the window from which it was opened
        try {
            driver.switchTo().window(
                    driver.getWindowHandles().iterator().next());
        } catch (NoSuchWindowException e) {
            driver.switchTo().window(
                    driver.getWindowHandles().iterator().next());
        }
    }

    public void makeScreenshot() {
        WebDriverUtils.makeScreenshot(driver);
    }

    public void makeRobotScreenshot() {
        WebDriverUtils.makeRobotScreenshot();
    }

    public void attachText(String name, String content) {
        Utils.attachText(name, content);
    }

    public void makeScreenshot(String message) {
        WebDriverUtils.makeScreenshot(driver, message);
    }

    protected WebElement waitFor(By by) {
        return WebDriverUtils.waitFor(by, driver);
    }

    protected void waitForCount(By by, int num) {
        WebDriverUtils.waitForCount(by, num, driver);
    }

    protected WebElement waitForTextChanged(By by, String text) {
        return WebDriverUtils.waitForTextChanged(by, text, driver);
    }

    /*
        Pass locator not the element to prevent StaleElementException
     */
    protected String waitForAttributeChanged(By by, String attr, String value) {
        return WebDriverUtils.waitForAttributeChanged(by, attr, value, driver);
    }

    protected String waitForSomeText(By by) {
        return WebDriverUtils.waitForSomeText(by, driver);
    }

    protected WebElement waitForShort(By by) {
        return WebDriverUtils.waitFor(by, driver, Const.SHORT_TIMEOUT, null);
    }

    protected WebElement waitForShort(By by, String message) {
        return WebDriverUtils.waitFor(by, driver, Const.SHORT_TIMEOUT, message);
    }

    protected WebElement waitFor(WebElement parent, By by) {
        return WebDriverUtils.waitFor(parent, by, driver);
    }

    protected WebElement waitForVisible(final By by) {
        return WebDriverUtils.waitForVisible(by, driver);
    }

    protected WebElement waitForVisibleShort(final By by) {
        return WebDriverUtils.waitForVisibleShort(by, driver);
    }

    protected void waitForNotVisible(final By by) {
        WebDriverUtils.waitForNotVisible(by, driver);
    }

    protected void waitForNotPresent(final By by) {
        WebDriverUtils.waitForNotPresent(by, driver);
    }
/*
    protected void waitForWindows(final int expectedWindowsNumber) {
        try {
            getWait(Const.TIMEOUT).until(new Predicate<WebDriver>() {
                public boolean apply(org.openqa.selenium.WebDriver driver) {
                    int windowsCount = driver.getWindowHandles().size();
                    if (windowsCount == expectedWindowsNumber) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Wrong number of windows: [" + driver.getWindowHandles().size() + "] when expected [" + expectedWindowsNumber + "]", e);
        }
    }
*/
    protected WebElement waitForInFrame(final By by, final String frame) {
        return WebDriverUtils.waitForInFrame(by, frame, driver);
    }
/*
    protected void openNewWindowWith(final WebElement opener) {
        Set<String> wndsBefore = driver.getWindowHandles();
        if (Config.get().IE()) {
            clickWithJS(opener, driver);
        } else {
            opener.click();
        }
        waitForWindows(wndsBefore.size() + 1);
        Set<String> wnds = driver.getWindowHandles();
        wnds.removeAll(wndsBefore);
        if (wnds.size() != 1) {
            throw new RuntimeException("Wrong number of newly opened windows: " + wnds.size());
        }
        driver.switchTo().window(wnds.iterator().next());
        WebDriverUtils.processCertWarning(driver);
    }
*/
    public void quit() {
        DriverManager.stop();
    }

    public void checkJSErrors() {
        WebDriverUtils.checkJSErrors(driver);
    }

    protected void __mark(By xpath) {
        WebDriverUtils.executeJS("arguments[0].setAttribute('atFlag', 'marked');", find(xpath), driver);
    }

    /*
    Wait until new element appeared. Element should be marked before performing action that will change the page.
     */
 /*   protected void waitForChanged(final By xpath, long timeout) {
        getWait(Const.SHORT_TIMEOUT).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                try {
                    WebElement el = find(xpath);
                    if (el.getAttribute("atFlag") == null) {
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
    
    protected void waitForChanged(final By xpath) {
        waitForChanged(xpath, Const.SHORT_TIMEOUT);
    }
*/
    /*
        Retrieve value from the cell of specified event and column
     */
    protected String cell(String eventID, String column) {
        for (int i = 0; i < 3; i++) {
            try {
                int columnNum = findAll(By.xpath("//th[./div/span[text()='" + column + "']]/preceding-sibling::th")).size() + 1;
                WebElement eventRow = null;
                if (Config.get().IE()) {
                    eventRow = waitFor(By.xpath("//tr[./td[text()='" + eventID + "']]"));
                } else {
                    eventRow = find(By.xpath("//tr[./td[text()='" + eventID + "']]"));
                }
                return eventRow.findElement(By.xpath("./td[" + columnNum + "]")).getText();
            } catch (Exception e) {
                // do nothing
                Utils.waitABit(1000);
            }
        }
        throw new RuntimeException("Unable to get cell value after 3 tries");
    }

}
