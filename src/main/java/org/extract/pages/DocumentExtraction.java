package org.extract.pages;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.extract.util.Waits;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class DocumentExtraction {

    private static final Logger logger = LogManager.getLogger(DocumentExtraction.class);
    private static final Path employeesFolder = Paths.get("./Employees");

    public static void documentExtraction(WebDriver driver, Path downloadDirPath, String eeCode) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy'\n'hh:mm:ss a");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        List<List<String>> lists = new ArrayList<>();
        List<String> listRow = new ArrayList<>();
        listRow.add("Employee Name");
        listRow.add("Document Name");
        listRow.add("Employee Acknowledgement");
        listRow.add("Supervisor Acknowledgement");
        lists.add(listRow);

        Waits.waitUntilVisible(driver,
                By.xpath(
                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[3]/div/div[2]/div/label/select")
        );
        List<WebElement> options = driver.findElements(By.xpath(
                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[3]/div/div[2]/div/label/select/option"));
        for (WebElement option : options) {
            if (option.getText().equalsIgnoreCase("500")) {
                Waits.waitUntilClickable(driver, option);
                break;
            }
        }
        Waits.waitUntilVisible(driver,
                By.xpath(
                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[1]")
        );
        // WE ARE ON THE DOCUMENTS PAGE
        List<WebElement> rows = driver.findElements(By.xpath(
                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr"));
        Path employeeDir = employeesFolder.resolve(eeCode);
        String name = driver.findElement(By.xpath(
                "/html/body/div[5]/div/div[1]/div[1]/div/div[1]/div[2]/div/div[1]/a")).getText();
        saveImage(driver.findElement(By.xpath(
                "/html/body/div[5]/div/div[1]/div[1]/div/div[1]/div[1]/a/img"))
                        .getAttribute("src"),
                employeeDir.resolve(eeCode + "_employee_photo_.jpg")
        );
        if (rows.size() > 1) {
            for (int i = 0, rowsSize = rows.size(); i < rowsSize; i++) {
                String title1 = "";
                String superAck = "";
                String employeeAck = "";
                for (int j = 0, colSize = driver.findElements(By.xpath(
                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                (i + 1) +
                                "]/td")).size();
                     j < colSize;
                     j++
                ) {
                    boolean contains;
                    try {
                        contains = driver.findElement(By.xpath(
                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                        (i + 1) +
                                        "]/td[" +
                                        (j + 1) +
                                        "]"))
                                .getText()
                                .contains("...");
                    }
                    catch (StaleElementReferenceException e) {
                        Waits.sleep(2);
                        contains = driver.findElement(By.xpath(
                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                        (i + 1) +
                                        "]/td[" +
                                        (j + 1) +
                                        "]"))
                                .getText()
                                .contains("...");
                    }
                    if (contains) {
                        List<WebElement> children = driver.findElements(By.xpath(
                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                        (i + 1) +
                                        "]/td[" +
                                        (j + 1) +
                                        "]//*"));
                        WebElement child = children.get(children.size() - 1);
                        if (j == 1) {
                            try {
                                title1 = child.getAttribute("title");
                            }
                            catch (StaleElementReferenceException e) {
                                Waits.sleep(2000);
                                title1 = driver.findElements(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/td[" +
                                                (j + 1) +
                                                "]//*")).get(driver.findElements(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/td[" +
                                                (j + 1) +
                                                "]//*")).size() - 1).getAttribute("title");
                            }
                        }
                        else if (j == 4) {
                            try {
                                employeeAck = child.getAttribute("title");
                            }
                            catch (StaleElementReferenceException e) {
                                Waits.sleep(2000);
                                employeeAck = driver.findElements(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/td[" +
                                                (j + 1) +
                                                "]//*")).get(driver.findElements(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/td[" +
                                                (j + 1) +
                                                "]//*")).size() - 1).getAttribute("title");
                            }
                        }
                        else if (j == 5) {
                            try {
                                superAck = child.getAttribute("title");
                            }
                            catch (StaleElementReferenceException e) {
                                Waits.sleep(2000);
                                superAck = driver.findElements(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/td[" +
                                                (j + 1) +
                                                "]//*")).get(driver.findElements(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/td[" +
                                                (j + 1) +
                                                "]//*")).size() - 1).getAttribute("title");
                            }
                        }
                    }
                    else {
                        if (j == 1) {
                            try {
                                title1 = driver.findElement(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/*[" +
                                                (j + 1) +
                                                "]"))
                                        .getText();
                            }
                            catch (StaleElementReferenceException e) {
                                Waits.sleep(2000);
                                title1 = driver.findElement(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/*[" +
                                                (j + 1) +
                                                "]"))
                                        .getText();
                            }
                        }
                        else if (j == 4) {
                            try {
                                employeeAck = driver.findElement(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/*[" +
                                                (j + 1) +
                                                "]"))
                                        .getText();
                            }
                            catch (StaleElementReferenceException e) {
                                Waits.sleep(2000);
                                employeeAck = driver.findElement(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/*[" +
                                                (j + 1) +
                                                "]"))
                                        .getText();
                            }
                        }
                        else if (j == 5) {
                            try {
                                superAck = driver.findElement(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/*[" +
                                                (j + 1) +
                                                "]"))
                                        .getText();
                            }
                            catch (StaleElementReferenceException e) {
                                Waits.sleep(2000);
                                superAck = driver.findElement(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/*[" +
                                                (j + 1) +
                                                "]"))
                                        .getText();
                            }
                        }
                    }
                    boolean linkSize;
                    try {
                        linkSize = driver.findElements(By.xpath(
                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                        (i + 1) +
                                        "]/*[" +
                                        (j + 1) +
                                        "]//*/a[@href]")).size() > 0;
                    }
                    catch (StaleElementReferenceException e) {
                        Waits.sleep(2000);
                        linkSize = driver.findElements(By.xpath(
                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                        (i + 1) +
                                        "]/*[" +
                                        (j + 1) +
                                        "]//*/a[@href]")).size() > 0;
                    }
                    if (linkSize) {
                        List<WebElement> links = driver.findElements(By.xpath(
                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                        (i + 1) +
                                        "]/*[" +
                                        (j + 1) +
                                        "]//*/a[@href]"));
                        for (int k = 0, linksSize = links.size(); k < linksSize; k++) {
                            WebElement link = links.get(k);
                            boolean resetStaleElement1 = false;
                            try {
                                resetStaleElement1 = !link.getAttribute("class").isBlank();
                            } catch (StaleElementReferenceException akjsfga) {
                                link = driver.findElements(By.xpath(
                                        "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                (i + 1) +
                                                "]/td[" +
                                                (j + 1) +
                                                "]//*/a[@href]")).get(k);
                                resetStaleElement1 = !link.getAttribute("class").isBlank();
                            }
                            if (resetStaleElement1) {
                                boolean resetStaleElement2 = false;
                                try {
                                    resetStaleElement2 = link.getAttribute("class").contains("popoverTrigger");
                                } catch (StaleElementReferenceException akjsfga) {
                                    link = driver.findElements(By.xpath(
                                            "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                    (i + 1) +
                                                    "]/td[" +
                                                    (j + 1) +
                                                    "]//*/a[@href]")).get(k);
                                    resetStaleElement2 = !link.getAttribute("class").isBlank();
                                }
                                if (resetStaleElement2) {
                                    try {
                                        new Actions(driver).moveToElement(link).click(link).perform();
                                    }
                                    catch (StaleElementReferenceException | ElementNotInteractableException e) {
                                        if (driver.findElements(By.xpath(
                                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                        (i + 1) +
                                                        "]/td[" +
                                                        (j + 1) +
                                                        "]//*/a[@href]/div")).size() > 0) {
                                            link = driver.findElements(By.xpath(
                                                    "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                            (i + 1) +
                                                            "]/td[" +
                                                            (j + 1) +
                                                            "]//*/a[@href]/div")).get(k);
                                        }
                                        else {
                                            link = driver.findElements(By.xpath(
                                                    "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                            (i + 1) +
                                                            "]/td[" +
                                                            (j + 1) +
                                                            "]//*/a[@href]")).get(k);
                                        }
                                        new Actions(driver).moveToElement(link).click(link).perform();
                                    }
                                    Waits.waitForLoad(driver);
                                    String img = "";
                                    String sig = "";
                                    try {
                                        img = driver.findElement(By.xpath(
                                                "/html/body/div[7]/div[2]/div/div/div/div[2]/div[2]/div/img"))
                                                .getAttribute("src");
                                    }
                                    catch (NoSuchElementException e) {
                                        try {
                                            img = driver.findElement(By.xpath(
                                                    "/html/body/div[7]/div[2]/div/div/div/div/div/div[2]/div/img"))
                                                    .getAttribute("src");
                                        }
                                        catch (NoSuchElementException exception) {
                                            try {
                                                sig = driver.findElement(By.xpath(
                                                        "/html/body/div[7]/div[2]/div/div/div/div[2]/div[2]"))
                                                        .getText();
                                            }
                                            catch (NoSuchElementException exception1) {
                                                try {
                                                    sig = driver.findElement(By.xpath(
                                                            "/html/body/div[7]/div[2]/div/div/div/div/div/div[2]"))
                                                            .getText();
                                                }
                                                catch (NoSuchElementException exception2) {
                                                    sig = "";
                                                }
                                            }
                                        }
                                    }
                                    if (!img.isBlank()) {
                                        if (j == 4) {
                                            saveImage(img,
                                                    employeeDir.resolve(eeCode +
                                                            "_employee_acknowledgement_" +
                                                            title1.toLowerCase(Locale.ROOT)
                                                                    .replace(" ", "_") +
                                                            ".jpg")
                                            );
                                        }
                                        else if (j == 5) {
                                            saveImage(img,
                                                    employeeDir.resolve(eeCode +
                                                            "_supervisor_acknowledgement_" +

                                                            title1.toLowerCase(Locale.ROOT)
                                                                    .replace(" ", "_") +
                                                            ".jpg")
                                            );
                                        }

                                    }
                                    else {
                                        if (j == 4) {
                                            saveSig(sig,
                                                    employeeDir.resolve(eeCode + "_employee_acknowledgement_" +
                                                            title1.toLowerCase(Locale.ROOT)
                                                                    .replace(" ", "_") +
                                                            ".txt")
                                            );
                                        }
                                        else if (j == 5) {
                                            saveSig(sig,
                                                    employeeDir.resolve(eeCode +
                                                            "_supervisor_acknowledgement_" +
                                                            title1.toLowerCase(Locale.ROOT)
                                                                    .replace(" ", "_") +
                                                            ".txt")
                                            );
                                        }
                                    }
                                    try {
                                        new Actions(driver).moveToElement(link).click(link).perform();
                                    }
                                    catch (StaleElementReferenceException e) {
                                        Waits.sleep(2000);
                                        link = driver.findElements(By.xpath(
                                                "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[3]/table/tbody/tr[" +
                                                        (i + 1) +
                                                        "]/td[" +
                                                        (j + 1) +
                                                        "]//*/a[@href]")).get(k);
                                        new Actions(driver).moveToElement(link).click(link).perform();
                                    }
                                }
                            }
                        }
                    }
                }
                List<String> list = new ArrayList<>();
                list.add(name);
                list.add(title1);
                try {
                    list.add(outputFormat.format(simpleDateFormat.parse(employeeAck)));
                }
                catch (ParseException e) {
                    list.add(employeeAck);
                }
                try {
                    list.add(outputFormat.format(simpleDateFormat.parse(superAck)));
                }
                catch (ParseException e) {
                    list.add(superAck);
                }
                lists.add(list);
            }
            new Actions(driver).keyDown(Keys.CONTROL).sendKeys(Keys.HOME).keyUp(Keys.CONTROL).perform();
            Waits.sleep(1000);
            Waits.waitUntilClickable(driver, By.xpath(
                    "//*[@id='ee-doc-table-select-all']"));
            Waits.waitUntilClickable(driver, By.xpath(
                    "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[1]/div[3]/span/div/div/a/span[1]"));
            Waits.waitUntilVisible(driver,
                    By.xpath(
                            "/html/body/div[5]/div/div[3]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/span")
            );
            driver.findElement(By.xpath("//*[@id='documentDownload']/a")).click();
            final Instant instant = Instant.now();
            boolean downloaded = false;
            try {
                new WebDriverWait(driver, 30).until(ExpectedConditions.elementToBeClickable(By.xpath(
                        "/html/body/div[5]/div/div[3]/div/div[2]/div[1]/div/div/div[3]/div[2]/input"))).click();
                downloaded = true;
            }
            catch (Exception e) {
                try {
                    new WebDriverWait(driver, 30).until(ExpectedConditions.elementToBeClickable(By.xpath(
                            "/html/body/div[5]/div/div[3]/div/div[2]/div/div/div/ul/li[1]/div/div/div[3]/div[2]/input")))
                            .click();
                    downloaded = true;
                }
                catch (Exception exception) {
                    logger.debug("Timeout", e);
                }
            }
            Waits.waitForLoad(driver);
            if (downloaded) {
                Waits.waitUntilFileDownloaded(driver, downloadDirPath.toFile(), 120000, "\\S{5}_.*?_eeDocuments_.*?\\.zip");
                Waits.sleep(1000);
                File file = Objects.requireNonNull(downloadDirPath.toFile()
                        .listFiles(pathname -> pathname.lastModified() >
                                instant.toEpochMilli()))[0];
                String downloadName = eeCode + "_eeDocuments.zip";
                file.renameTo(employeeDir.resolve(downloadName).toFile());
            }
            driver.findElement(By.xpath(
                    "/html/body/div[5]/div/div[3]/div/div[2]/div[1]/div/div/div[3]/div[2]/div[1]/div/input"))
                    .click();
            Waits.waitForLoad(driver);
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

    public static void saveImage(String src, Path file) {
        saveImage(src, file.toFile());
    }

    public static void saveImage(String src, File file) {
        if (src.contains("https://")) {
            try {
                URL url = new URL(src);
                FileUtils.copyURLToFile(url, file);
            }
            catch (MalformedURLException e) {
                logger.fatal("Invalid URL", e);
                System.exit(1);
            }
            catch (IOException e) {
                logger.fatal("Unable to save image", e);
                System.exit(1);
            }
        }
        else {
            byte[] imgBytes = Base64.getDecoder().decode(src.replaceAll("^\\S+base64,", ""));
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(imgBytes);
                fileOutputStream.close();
            }
            catch (IOException e) {
                logger.fatal("Unable to save image", e);
                System.exit(1);
            }
        }
    }

    public static void saveSig(String sig, Path file) {
        saveSig(sig, file.toFile());
    }

    public static void saveSig(String sig, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(sig);
            writer.close();
        }
        catch (IOException e) {
            logger.fatal("Unable to save signature", e);
            System.exit(1);
        }
    }
}
