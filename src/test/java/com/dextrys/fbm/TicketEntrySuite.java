package com.dextrys.fbm;

import com.dextrys.DriverManager;
import com.dextrys.annotations.Browser;
import com.dextrys.steps.sem.BasicSteps;
import com.dextrys.steps.sem.SearchSteps;
//import com.dextrys.utils.Assert;
//import com.dextrys.utils.Rand;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

//import ru.yandex.qatools.allure.annotations.Title;

import java.lang.reflect.Method;
//import java.util.LinkedHashMap;
//import java.util.Map;

import static com.dextrys.config.Const.TEST_TIMEOUT;
//import static com.dextrys.utils.Utils.map;

//@Title("Ticket entry tests")
public class TicketEntrySuite {

    private static final Logger logger = LoggerFactory.getLogger(TicketEntrySuite.class);

    private BasicSteps common;
    private SearchSteps steps;
 

    @BeforeMethod
    public void setUp(Method method) throws Exception {
        WebDriver driver = DriverManager.getDriver(method);
        common = new BasicSteps(driver);
        steps = new SearchSteps(driver);
 
    }

    @AfterSuite
    public void tearDown() throws Exception {
        try {
            common.quit();
        } catch (Exception e) {
            logger.info("Unable to close driver: " + e.getMessage());
        }
    }




   // @Title("xxxx1")
    @Test(description = "TC1xxxxxxxxxxxxxxx", timeOut = TEST_TIMEOUT)
    @Browser(reopen = true)
    public void inboxTest() throws Exception {
        common.loginAs();
    }
  //  @Title("xxxx2")
    @Test(description = "TC2xxxxxxxxxxxxxxx", timeOut = TEST_TIMEOUT)
    public void searchTest() throws Exception {
        steps.search();
    }
  
}
