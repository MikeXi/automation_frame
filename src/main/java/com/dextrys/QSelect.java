package com.dextrys;

//import com.google.common.base.Function;
//import com.google.common.base.Predicate;
import com.dextrys.config.Config;
//import com.dextrys.config.Const;
import com.dextrys.utils.WebDriverUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
//import java.util.Random;

//import static com.dextrys.utils.Utils.report;
//import static com.dextrys.utils.WebDriverUtils.getWait;

public class QSelect extends Select {
    private static final Logger logger = LoggerFactory.getLogger(QSelect.class);
    private final WebDriver _driver;
    private final WebElement _element;

    public QSelect(WebElement element, WebDriver driver) {
        super(element);
        _element = element;
        _driver = driver;
    }

    /**
     * Cross-browser method to select SINGLE option from dropdown
     * @param index
     */
    @Override
    public void selectByIndex(int index) {
        if (Config.get().IE()) {
            // find index of selected option
            int selected = 0;
            List<WebElement> options = _element.findElements(By.xpath("./option"));
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).getAttribute("selected") != null) {
                    selected = i;
                    break;
                }
            }
            // calculate direction
            Keys key = null;
            if (selected > index) {
                key = Keys.ARROW_UP;
            } else {
                key = Keys.ARROW_DOWN;
            }
            // focus on the select
            _element.sendKeys(Keys.SPACE);
            for (int j = 0; j < Math.abs(selected - index); j++) {
                _element.sendKeys(key);
            }
            _element.sendKeys(Keys.ENTER);
        } else {
            super.selectByIndex(index);
        }
    }
/*
    public String selectRandomOption() {
        List<WebElement> options = null;
        try {
            options = getWait(_driver, Const.SHORT_TIMEOUT, Const.TIMEOUT_POLL).until(new Function<WebDriver, List<WebElement>>() {
                public List<WebElement> apply(WebDriver driver) {
                    List<WebElement> tmp = getOptions();
                    if (tmp.size() <= 1) {
                        return null;
                    } else {
                        return tmp;
                    }
                }
            });
        } catch (TimeoutException e) {
            throw new RuntimeException("No options to change selection");
        }
        String selected = "";
        try {   // there is not default option for multi selects
            selected = this.getFirstSelectedOption().getText();
        } catch (Exception e) {}
        String optionLabel = selected;
        Integer idx = null;
        int i = 0;
        while (selected.equals(optionLabel)) {
            idx = new Random().nextInt(options.size());
            try {
                optionLabel = getOptions().get(idx).getText();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if (i++ == 10) {
                throw new RuntimeException("Unable to change selection after 10 tries");
            }
        }
        this.selectByIndex(idx);

        report(optionLabel); // TODO remove dependency?
        return optionLabel;
    }
*/
    /**
     * TODO multiple implementation
     * @param text
     */
    @Override
    public void selectByVisibleText(String text) {
        if (Config.get().IE()) {
            WebElement option = _element.findElement(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(text) + "]"));
            WebDriverUtils.executeJS("arguments[0].selected = true;", option, _driver);
        } else {
            super.selectByVisibleText(text);
        }
    }

    public void selectByVisibleTextUsingKeys(String text) {
        List<WebElement> opts = getOptions();
        int idx = -1;
        for (int i = 0; i < opts.size(); i++) {
            if (opts.get(i).getText().equals(text)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            throw new RuntimeException("Option is not present: " + text);
        } else {
            if (Config.get().useNativeEvents()) {
                selectByIndex(idx);
            } else {
                selectByVisibleText(text);
                WebDriverUtils.fireHTMLEvent(_element, "change", _driver);
 
            }
        }
    }

    /*
    Wait for at least minNum options appeared in the select
     */
    /*
    public void waitForOptions(final int minNum) {
        new WebDriverWait(_driver, Const.SHORT_TIMEOUT).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return getOptions().size() >= minNum;
            }
        });
    }
    */
}
