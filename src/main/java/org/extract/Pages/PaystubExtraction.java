package org.extract.Pages;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.extract.Util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PaystubExtraction {

    private static final Logger logger = LogManager.getLogger(PaystubExtraction.class);
    private static final Path employeesFolder = Paths.get("./Employees");

    public static void paystubExtraction(WebDriver driver, Path downloadDirPath, String eeCode, WebDriverWait wait) {

        List<WebElement> options;
        options = driver.findElements(By.xpath("//select[@id='yearCheck']/option"));
        Waits.waitForLoad(driver);
        for (WebElement option : options) {
            if (option.getText().equalsIgnoreCase("Custom")) {
                Waits.waitUntilClickable(driver, option);
                break;
            }
        }

        // CUSTOM DATE RANGE
        String startDateInput = "01011900";
        String endDateInput = "12312021";
        Waits.waitUntilClickable(driver, By.xpath("//*[@name='enddate']"));
        Waits.waitUntilClickable(driver, By.xpath("//*[@name='startdate']"));
        WebElement startDateBox = driver.findElement(By.xpath("//*[@name='startdate']"));
        WebElement endDateBox = driver.findElement(By.xpath("//*[@name='enddate']"));
        startDateBox.sendKeys(startDateInput);
        Waits.waitUntilClickable(driver, By.xpath("//*[@name='enddate']"));
        endDateBox.sendKeys(endDateInput);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        // Date range has been set from 01/01/1900 - 12/31/2021

        // We wait for the files from the "Custom" time period to finish loading. When the loop breaks we move on.
        while (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='check-listings-table_processing']")))) {
        }
        List<WebElement> paystubTableRows = driver.findElements(By.xpath("//table[@id='check-listings-table']/tbody/tr[@role='row']"));
        if (paystubTableRows.size() == 0) {
            return;
        }

        // Select all Payroll profiles available
        Waits.waitUntilClickable(driver, By.xpath("//input[@id='check-listings-table-select-all']"));
        while (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='check-listings-table_processing']")))) {
        }
        Waits.waitUntilClickable(driver, By.xpath("//a[@id='viewchecks']"));

        Waits.waitUntilFileDownloaded(driver, downloadDirPath.toFile(), 60000, "^.*earnstatement.*pdf$");
        Waits.sleep(1000);

        Path employeeFolder = employeesFolder.resolve(eeCode);
        File[] files = downloadDirPath.toFile().listFiles();
        for (File file : files) {
            String downloadName = eeCode + "_earnstatements.pdf";
            if (file.toString().matches("^.*earnstatement.*pdf$")){
                file.renameTo(employeeFolder.resolve(downloadName).toFile());
                break;
            }
        }
        try {
            if (downloadDirPath.toFile().exists()) {
                FileUtils.cleanDirectory(downloadDirPath.toFile());
                Waits.sleep(1000);
            }
        } catch (IOException e) {
            logger.fatal("Cleaning was unsuccessful", e);
            System.exit(1);
        }
    }
}
