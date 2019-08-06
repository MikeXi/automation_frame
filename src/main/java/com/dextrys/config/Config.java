package com.dextrys.config;

//import com.dextrys.DriverManager;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
//import org.apache.commons.configuration.tree.ConfigurationNode;
//import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.util.Iterator;
 
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private static final Config INSTANCE = new Config();

    private String _environment = Const.DEFAULT_ENVIRONMENT;
    private boolean _checkJS = false;
    private String _browser = "";
    private boolean _withProxy;
    private String _facility = null;
    private XMLConfiguration _auth;
    private ScreenshotStrategy _screenshotStrategy;
    private int _browserRestart = Const.BROWSER_RESTART;
    private String lastTestStatus = "nothing";
    private double _timeoutMultiplier;
    private boolean _extendedScreenshots;
    private boolean _enableScrolling = Const.SCROLLING;
    private boolean _nativeEvents = Const.USE_IE_NATIVE_EVENTS;

    private Config() {
        super();
    }

    public static Config get() {
        return INSTANCE;
    }

    public int getBrowserRestart() {
        return _browserRestart;
    }

    public void initialize() {
        if (System.getProperty(Const.BROWSER_PARAM) == null) {
            _browser = Const.DEFAULT_BROWSER;
        } else {
            _browser = System.getProperty(Const.BROWSER_PARAM);
        }
        if (System.getProperty(Const.ENV_PARAM) != null) {
            _environment = System.getProperty(Const.ENV_PARAM);
        }
        if (System.getProperty(Const.CHECK_JS_PARAM) != null) {
            _checkJS = Boolean.parseBoolean(System.getProperty(Const.CHECK_JS_PARAM));
        }
        if (System.getProperty(Const.BROWSER_RESTART_PARAM) != null) {
            _browserRestart = Integer.parseInt(System.getProperty(Const.BROWSER_RESTART_PARAM));
        }
        if (System.getProperty(Const.SCREENSHOT_PARAM) == null) {
            _screenshotStrategy = ScreenshotStrategy.ALL;
        } else {
            _screenshotStrategy = ScreenshotStrategy.parse(System.getProperty(Const.SCREENSHOT_PARAM));
        }
        if (System.getProperty(Const.EXTENDED_SCREENSHOTS_PARAM) != null) {
            _extendedScreenshots = true;
        } else {
            _extendedScreenshots = false;
        }
        if (System.getProperty(Const.USE_IE_NATIVE_EVENTS_PARAM) != null) {
            _nativeEvents = Boolean.parseBoolean(System.getProperty(Const.USE_IE_NATIVE_EVENTS_PARAM));
            logger.info("[NATIVE_EVENTS] " + _nativeEvents);
        }
        try {
            _auth = new XMLConfiguration();
            _auth.setDelimiterParsingDisabled(true);
            _auth.load("auth.xml");
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to load properties: auth.xml", e);
        }
        logger.info("[BROWSER] " + _browser);
        logger.info("[RESTART] " + _browserRestart);
        logger.info("[ENV] " + _environment);
        logger.info("[TIMEOUT] " + Const.TIMEOUT);
        logger.info("[SCREENSHOT] " + _screenshotStrategy);
        logger.info("[CHECKJS] " + _checkJS);
    }

    public String getEnvironment() {
        return _environment;
    }

    public String getFacility() {
        return _facility;
    }

    public boolean checkJS() {
        return _checkJS;
    }

    public boolean IE() {
        return _browser.equals("ie");
    }
    
    public boolean Firefox() {
        return _browser.toLowerCase().equals("firefox");
    }

    public boolean withProxy() {
        return _withProxy;
    }

    public void setProxy(boolean withProxy) {
        _withProxy = withProxy;
    }


    public ScreenshotStrategy screenshotStrategy() {
        return _screenshotStrategy;
    }

    public void setLastTestStatus(String status) {
        this.lastTestStatus = status;
    }

    public String getLastTestStatus() {
        return lastTestStatus;
    }

    public void setTimeoutMultiplier(double timeoutMultiplier) {
        this._timeoutMultiplier = timeoutMultiplier;
    }

    public double getTimeoutMultiplier() {
        return _timeoutMultiplier;
    }

    public boolean extendedScreenshots() {
        return _extendedScreenshots;
    }

    public boolean isScrollingEnabled() {
        return _enableScrolling;
    }

    public void enableScrolling(boolean b) {
        this._enableScrolling = b;
    }

    public boolean useNativeEvents() {
        return this._nativeEvents;
    }
}
