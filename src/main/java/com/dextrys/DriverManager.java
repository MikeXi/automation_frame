package com.dextrys;

import com.dextrys.annotations.Browser;
import com.dextrys.annotations.Timeout;
import com.dextrys.annotations.WithProxy;
import com.dextrys.config.Config;
import com.dextrys.config.Const;
import com.dextrys.utils.ByteStorage;
import com.dextrys.utils.Utils;

import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);

    private static DesiredCapabilities capabilities = new DesiredCapabilities();
    private static WebDriver _driver;
    private static BrowserMobProxy _proxy;

    private static WebDriver start(Method method) {
        if (method != null && method.isAnnotationPresent(Timeout.class)) {
            Timeout annotation = method.getAnnotation(Timeout.class);
            double multiplier = annotation.mupltiplier();
            Config.get().setTimeoutMultiplier(multiplier);
        } else {
            Config.get().setTimeoutMultiplier(1.0);
        }

        if (method != null && method.isAnnotationPresent(WithProxy.class)) {
            Config.get().setProxy(true);
            _proxy = new BrowserMobProxyServer();
            _proxy.start();
            capabilities.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(_proxy));

            WithProxy annotation = method.getAnnotation(WithProxy.class);
            final String type = annotation.filterType();

            ByteStorage.map.clear();
            _proxy.addResponseFilter(new ResponseFilter() {
                public void filterResponse(HttpResponse httpResponse, HttpMessageContents httpMessageContents, HttpMessageInfo httpMessageInfo) {
                    if (ByteStorage.isEnabled()) {
                        if (httpMessageContents.getContentType().contains(type)) {
                            String filename = Utils.getParameter(httpMessageInfo.getOriginalUrl(), "PageName");
                            ByteStorage.map.put(filename, httpMessageContents.getBinaryContents());
                        }
                    }
                }
            });
        } else {
            // on VM with IE9 proxy settings are not returned back, so we need to flush them manually
            org.openqa.selenium.Proxy noProxy = new org.openqa.selenium.Proxy();
            noProxy.setProxyType(Proxy.ProxyType.DIRECT); // TODO be careful with nodes that use proxy
            capabilities.setCapability(CapabilityType.PROXY, noProxy);
            Config.get().setProxy(false);
        }

        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
        // TODO remote hardcode if required
        URL remoteURL = null;
        try {
            remoteURL = new URL("http://172.16.242.20:4444/wd/hub");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        // to run on the remote server
        boolean remote = false;

        if (Config.get().IE()) {
            if (System.getProperty("webdriver.ie.driver") == null) {
                System.setProperty("webdriver.ie.driver", Const.IEDRIVER_PATH);
            }
            capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
            capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
            capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, Config.get().useNativeEvents());
           
            if (remote) {
                // for remote debug (tests with proxy are NOT working - proxy on the different machine) !!!
                capabilities.setBrowserName("internet explorer");
                _driver = new RemoteWebDriver(remoteURL, capabilities);
            } else {
                _driver = new InternetExplorerDriver(capabilities);
            }
        } else if(Config.get().Firefox()){
        	
        	
        	_driver = new FirefoxDriver();
        	_driver.manage().window().maximize();
        	
        } else {
            if (System.getProperty("webdriver.chrome.driver") == null) {
                System.setProperty("webdriver.chrome.driver", Const.CHROMEDRIVER_PATH);
            }
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-popup-blocking");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            if (remote) {
                capabilities.setBrowserName("chrome");
                _driver = new RemoteWebDriver(remoteURL, capabilities);
            } else {
                _driver = new ChromeDriver(capabilities);
            }
        }
        return _driver;
    }

    private static String getLogFilePath() {
        File file = new File(System.getProperty("webdriver.ie.driver"));
        return file.getParent() + File.separatorChar + "iedriverserver.log";
    }

    private static int run = 1;
    public static WebDriver getDriver(Method method) {
        boolean reopen = false;
  
        // check if browser opened too long
        if ( (run % Config.get().getBrowserRestart()) == 0 ) {
            reopen = true;
            run++;
            logger.debug("[BROWSER] Reopen: shabby browser");
        }
        // reopen browser after failed tests, because app state could be unpredictable
        if (Config.get().getLastTestStatus().equals("failed")) {
            reopen = true;
            logger.debug("[BROWSER] Reopen: failed test");
        }
        // reopen browser with proxy if required, or without - if no need in proxy anymore
        boolean proxyRequired = method != null && method.isAnnotationPresent(WithProxy.class);
        boolean driverWithProxy = Config.get().withProxy();
        if (proxyRequired != driverWithProxy) {
            reopen = true;
            logger.debug("[BROWSER] Reopen: change proxy");
        }
        // force reopen
        if (method != null && method.isAnnotationPresent(Browser.class)) {
            Browser annotation = method.getAnnotation(Browser.class);
            if (annotation.reopen()) {
                reopen = true;
                logger.debug("[BROWSER] Reopen: force");
            }
        }

        if (reopen) {
            try {
                stop();
            } catch (Exception e) {}
            return start(method);
        } else {
            logger.info("[BROWSER] Using current browser");
            return _driver;
        }
    }

    public static void stop() {
        _driver.quit();
        if (Config.get().withProxy()) {
            _proxy.stop();
        }
    }

    public static WebDriver get() {
        return _driver;
    }

    public static BrowserMobProxy getProxy() {
        return _proxy;
    }
}
