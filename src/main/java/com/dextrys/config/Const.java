package com.dextrys.config;

 
public class Const {

    public static final long TIMEOUT = 30;
    public static final long SHORT_TIMEOUT = 10;
    public static final long TIMEOUT_POLL = 1000;
    public static final long TEST_TIMEOUT = 300000;
    public static final long FILE_TIMEOUT = 60;
    public static final long MAIL_TIMEOUT = 30;
    public static final String CHROMEDRIVER_PATH = ".\\drivers\\chromedriver.exe";
    public static final String IEDRIVER_PATH = "C:\\iedriver\\IEDriverServer.exe";


    public static final String DEFAULT_BROWSER = "chrome";
    public static final int BROWSER_RESTART = 10;
    public static final String DEFAULT_ENVIRONMENT = "https://www.baidu.com";

    public static final String ENV_PARAM = "env";
    public static final String CHECK_JS_PARAM = "checkJS";
    public static final String BROWSER_PARAM = "browser";
 
    public static final String SCREENSHOT_PARAM = "screenshot";
    public static final String BROWSER_RESTART_PARAM = "browserRestart";
    public static final String USE_IE_NATIVE_EVENTS_PARAM = "nativeEvents";
 
 
    public static final String ATTACHMENT = "testAttachment.png";
    public static final int AUTO_CLASSIFICATION_TIMEOUT = 120;
    public static final long INCIDENT_ACTION_TIMEOUT = TIMEOUT * 2;
    public static final String EXTENDED_SCREENSHOTS_PARAM = "extendedScreenshots";
    public static final boolean SCROLLING = true;
    public static final boolean USE_IE_NATIVE_EVENTS = true;
}
