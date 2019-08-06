package com.dextrys.steps.sem;

//import com.google.common.base.Predicate;
//import com.dextrys.QSelect;
import com.dextrys.config.Config;
//import com.dextrys.config.Const;
import com.dextrys.steps.WebDriverSteps;
//import com.dextrys.utils.DBHelper;
//import com.dextrys.utils.WebDriverUtils;
import org.openqa.selenium.*;
//import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.annotations.Step;

//import java.util.List;
//import java.util.Map;

//import static com.dextrys.utils.Utils.report;
//import static com.dextrys.utils.Utils.waitABit;

public class BasicSteps extends WebDriverSteps {

    private static final Logger logger = LoggerFactory.getLogger(BasicSteps.class);
    public BasicSteps(WebDriver driver) {
        super(driver);
    }

    @Step("Login as {0}")
    public void loginAs() {
    	String url = Config.get().getEnvironment();
        driver.navigate().to(url);
        makeScreenshot();
    }

    @Step("Log out")
    public void logOut() {
        driver.switchTo().parentFrame();
       _switchToMenuFrame();
        find(By.linkText("log out")).click();
    }

   
  
}
