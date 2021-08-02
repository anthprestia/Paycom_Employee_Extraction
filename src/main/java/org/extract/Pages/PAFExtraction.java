package org.extract.Pages;

import org.extract.Util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class PAFExtraction {

    private static final Path employeesFolder = Paths.get("./Employees");

    public static void pafExtraction(WebDriver driver, Path downloadDirPath, WebDriverWait wait) {

        Actions hover = new Actions(driver);
        hover.moveToElement(driver.findElement(By.id("TalentManagement"))).perform();
        Waits.sleep(100);
        hover.moveToElement(driver.findElement(By.id("PersonnelActionForms"))).perform();
        Waits.waitUntilClickable(driver, By.xpath("//*[@id=\"PersonnelActionFormDashboard\"]"));
        Waits.waitUntilClickable(driver, By.xpath("//*[@id=\"tabfinal-approved-tab\"]/a/span"));

        List<WebElement> options = driver.findElements(By.xpath("//*[@id=\"finaltable_length\"]/label/select/option"));
        Waits.waitForLoad(driver);
        for (WebElement option : options) {
            if (option.getText().equalsIgnoreCase("500")) {
                Waits.waitUntilClickable(driver, option);
                break;
            }
        }
        while (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='finaltable_processing']")))) {
        }

        File[] employees = employeesFolder.toFile().listFiles();

        for (File employee : employees) {
            String eeID = employee.getName();
            driver.findElement(By.xpath("//*[@id=\"finaltable_filter\"]/label/div/input")).sendKeys(eeID);
            while (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='finaltable_processing']")))) {
            }
            Waits.waitUntilClickable(driver, By.xpath("//*[@id=\"finaltable-select-all\"]"));
            while (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='finaltable_processing']")))) {
            }
            String tabName = driver.getWindowHandle();
            Waits.waitUntilClickable(driver, By.xpath("/html/body/div[4]/div/form/div[2]/div[2]/div[2]/div[1]/div[1]/div/div/a"));
            Waits.waitUntilClickable(driver, By.xpath("/html/body/div[4]/div/form/div[2]/div[2]/div[2]/div[1]/div[1]/div/div/ul/li/a"));
            // WE ARE NOW ON THE PRINT PREVIEW SCREEN

            Waits.waitForLoad(driver);


            // WE ARE NOW LEAVING THE PRINT PREVIEW SCREEN

            driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"\t");
            driver.switchTo().window(tabName);

            // WE HAVE LEFT THE PRINT PREVIEW SCREEN

            // IM GONNA MANUALLY GO AND HIT PRINT CUZ ITS NOT AUTOMATING CORRECTLY.
            // Make it so it does everything but that so I just click then it downloads, and then files it away correctly.
            // look at paystub 66 for download stuff


            Waits.waitUntilClickable(driver, By.xpath("//*[@id=\"finaltable-select-all\"]"));
            while (!wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='finaltable_processing']")))) {
            }
            driver.findElement(By.xpath("//*[@id=\"finaltable_filter\"]/label/div/input")).clear();

            String texas = "Sandy Cheeks";
        }

    }

}
