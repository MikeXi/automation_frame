package com.dextrys;

import com.dextrys.config.Config;
import com.dextrys.utils.WebDriverUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

import java.util.ArrayList;
import java.util.List;

public class WebElementWrapper implements WebElement, WrapsElement, Locatable{

    private final WebElement element;

    public WebElementWrapper(WebElement element) {
        if (element instanceof WebElementWrapper) {
            this.element = ((WebElementWrapper) element).getWrappedElement();
        } else {
            this.element = element;
        }
    }

    private void checkJSErrors() { // TODO may be better to initiate with desired driver? Think about parallel executions
        WebDriverUtils.checkJSErrors(DriverManager.get());
    }

    public void click() {
        try {
            WebDriverUtils.scrollTo(element, DriverManager.get());
        } catch (Exception e) {
            // do nothing
        }
        if (Config.get().IE()) {
            WebDriverUtils.clickWithJS(element, DriverManager.get());
        } else {
            element.click();
        }
        checkJSErrors();
    }

    public void submit() {
        element.click();
    }

    public void sendKeys(CharSequence... keysToSend) {
        try {
            WebDriverUtils.scrollTo(element, DriverManager.get());
        } catch (Exception e) {}
        element.sendKeys(keysToSend);
    }

    public void clear() {
        element.clear();
    }

    public String getTagName() {
        return element.getTagName();
    }

    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    public boolean isSelected() {
        return element.isSelected();
    }

    public boolean isEnabled() {
        return element.isEnabled();
    }

    public String getText() {
        return element.getText();
    }

    public List<WebElement> findElements(By by) {
        List<WebElement> elements = element.findElements(by);
        List<WebElement> wrappedElements = new ArrayList<WebElement>();
        for (WebElement element : elements) {
            wrappedElements.add(new WebElementWrapper(element));
        }
        return wrappedElements;
    }

    public WebElement findElement(By by) {
        return new WebElementWrapper(element.findElement(by));
    }

    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    public Point getLocation() {
        return element.getLocation();
    }

    public Dimension getSize() {
        return element.getSize();
    }

    public Rectangle getRect() {
        return element.getRect();
    }

    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    public WebElement getWrappedElement() {
        return element;
    }

    public Coordinates getCoordinates() {
        return ((Locatable) element).getCoordinates();
    }

    public <X> X getScreenshotAs(OutputType<X> xOutputType) throws WebDriverException {
        return element.getScreenshotAs(xOutputType);
    }
}
