package org.extract.util;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileFilter;
import java.time.Duration;

public class Waits {

    private static final Logger logger = LogManager.getLogger(Waits.class);
    public static void waitForLoad(WebDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) driver1).executeScript(
                "return document.readyState").equals("complete");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(pageLoadCondition);
    }

    public static void waitUntilClickable(WebDriver driver, By by) {
        WebDriverWait driverWait = new WebDriverWait(driver, 30);
        driverWait.until(ExpectedConditions.elementToBeClickable(by)).click();
        waitForLoad(driver);
    }

    public static void waitUntilClickable(WebDriver driver, WebElement element) {
        WebDriverWait driverWait = new WebDriverWait(driver, 30);
        driverWait.until(ExpectedConditions.elementToBeClickable(element)).click();
        waitForLoad(driver);
    }

    public static void waitUntilFileDownloaded(WebDriver driver, File downloadDir, long timeout, String pattern) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofMillis(timeout))
                .pollingEvery(Duration.ofMillis(200L));
        RegexFileFilter fileFilter = new RegexFileFilter(pattern);
        wait.until(driver1 -> {
            File[] files = downloadDir.listFiles((FileFilter) fileFilter);
            return (files != null && files.length > 0);
        });
    }

    public static void waitUntilVisible(WebDriver driver, By by) {
        WebDriverWait driverWait = new WebDriverWait(driver, 30);
        try {
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        }
        catch (TimeoutException e) {
            logger.debug("Timeout", e);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
