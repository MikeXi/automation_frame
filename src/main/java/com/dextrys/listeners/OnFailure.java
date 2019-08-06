package com.dextrys.listeners;

import com.dextrys.DriverManager;
import com.dextrys.config.Config;
import com.dextrys.config.Const;
import com.dextrys.config.ScreenshotStrategy;
import com.dextrys.utils.Utils;
import com.dextrys.utils.WebDriverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import ru.yandex.qatools.allure.annotations.Attachment;


public class OnFailure extends TestListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(OnFailure.class);

    @Override
    public void onTestFailure(ITestResult tr) {
        onFailureRoutine();
    }

    @Override
    public void onConfigurationFailure(ITestResult tr) {
        onFailureRoutine();
    }

    public void onFailureRoutine() {
        Config.get().enableScrolling(Const.SCROLLING); // put back to default
        try {
            Utils.report("Windows present: " + DriverManager.get().getWindowHandles().size());
        } catch (Exception e) {
            // do nothing
        }
        try {
            String alert = DriverManager.get().switchTo().alert().getText();
            Utils.attachText("Unexpected alert", alert);
        } catch (Exception e) { }
        if (!Config.get().screenshotStrategy().equals(ScreenshotStrategy.NONE)) {
            createScreenshot();
            if (Config.get().extendedScreenshots()) {
                createRobotScreenshot();
            }
        }
        createPageDump();
        DriverManager.stop();
        Config.get().setLastTestStatus("failed");
    }

    @Attachment(value = "OnFailure screenshot", type = "image/png")
    public byte[] createScreenshot() {
        byte[] imageBytes = WebDriverUtils.fullPageScreenshot();
        return imageBytes;
    }

    @Attachment(value = "OnFailure Robot screenshot", type = "image/png")
    public byte[] createRobotScreenshot() {
        byte[] bytes = new byte[0];
        try {
            bytes = WebDriverUtils.doRobotScreenshot();
        } catch (Exception e) {
            logger.error("Unable to make screenshot with Robot", e);
        }
        return bytes;
    }

    @Attachment(value = "Page source", type = "text/plain")
    public byte[] createPageDump() {
        try {
            return DriverManager.get().getPageSource().getBytes();
        } catch (Exception e) {
            // TODO log this
            return new byte[0];
        }
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        Config.get().setLastTestStatus("skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult tr) {
        Config.get().setLastTestStatus("failed");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        Config.get().setLastTestStatus("passed");
    }
}