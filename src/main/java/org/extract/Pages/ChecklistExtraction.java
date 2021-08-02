package org.extract.Pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.extract.Data.TableRows;
import org.extract.Util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChecklistExtraction {

    private static final Logger logger = LogManager.getLogger(ChecklistExtraction.class);
    private static final Path employeesFolder = Paths.get("./Employees");

    public static void checklistExtraction(WebDriver driver, Path downloadDirPath, String eeCode, WebDriverWait wait) {
        List<WebElement> paycomChecklist = driver.findElements(By.xpath("//table[@id='Checklist_Table']/tbody/tr"));

        // Empty workbook to work in.
        XSSFWorkbook workbook = new XSSFWorkbook();
        // If there is a row with empty data we move onto the next person
        if (paycomChecklist.size() == 1) {
            if (paycomChecklist.get(0).findElements(By.xpath("./td[@class='noData']")).size() > 0) {
                return;
            }
        }
        // Loop through each checklist
        for (int i = 0; i < paycomChecklist.size(); i++) {
            WebElement row = driver.findElement(By.xpath("//table[@id='Checklist_Table']/tbody/tr[" + (i+1) + "]"));
            // get the row
            String eeListID = row.findElement(By.xpath("./td[2]")).getText();
            String checklistDescription = row.findElement(By.xpath("./td[4]/a")).getText();
            // click the checklist
            Waits.waitUntilClickable(driver, row.findElement(By.xpath("./td[4]/a")));
            // We clicked on the hyper-link
            List<TableRows> tableRows = new ArrayList<>();
            String sheetName = checklistDescription;

            if (sheetName.length() > 31 || sheetName.matches(".*[\\/\\\\\\*\\?\\:\\[\\]].*")) {
                sheetName = eeListID;
            }
            List<WebElement> paycomSpecificChecklist = driver.findElements(By.xpath("//div[@class='table-responsive tableContainer  ']/table/tbody/tr"));
            // Access a worksheet for each checklist
            XSSFSheet worksheet;
            try {
                worksheet = workbook.createSheet(sheetName);
            } catch (IllegalArgumentException e) {
                worksheet = workbook.createSheet(eeListID);
            }

            for (WebElement checklistRow : paycomSpecificChecklist) {
                if (checklistRow.getAttribute("data-row-id") == null) {
                    break;
                }
                boolean completed;  // 1
                boolean startTask;  // 9
                String eeTaskId = checklistRow.findElement(By.xpath("./td[2]")).getText();      // 2
                String taskId = checklistRow.findElement(By.xpath("./td[3]")).getText();        // 3
                String description = checklistRow.findElement(By.xpath("./td[4]")).getText();   // 4
                String taskType = checklistRow.findElement(By.xpath("./td[5]")).getText();      // 5
                String taskFor = checklistRow.findElement(By.xpath("./td[6]")).getText();       // 6
                String completedBy = checklistRow.findElement(By.xpath("./td[7]")).getText();   // 7
                String timeCompleted = checklistRow.findElement(By.xpath("./td[8]")).getText(); // 8

                startTask = false;
                completed = true;
                if (checklistRow.findElements(By.xpath("./td[9]/a/img")).size() > 0) {
                    if (checklistRow.findElement(By.xpath("./td[9]/a/img")).getAttribute("src")
                            .equals("https://www.paycomonline.net/v4/ee/images/green_checkmark.png")) {
                        startTask = true;
                    }
                }
                if (completedBy.equals("")) {
                    completed = false;
                }
                tableRows.add(new TableRows(completed, eeTaskId, taskId, description, taskType, taskFor, completedBy, timeCompleted, startTask));

            }

            int rowCount = 0;
            XSSFRow rowElement = worksheet.createRow(rowCount);
            rowElement.createCell(0).setCellValue("Complete");
            rowElement.createCell(1).setCellValue("EE Task ID");
            rowElement.createCell(2).setCellValue("Task ID");
            rowElement.createCell(3).setCellValue("Task Description");
            rowElement.createCell(4).setCellValue("Task Type");
            rowElement.createCell(5).setCellValue("Task For");
            rowElement.createCell(6).setCellValue("Completed By");
            rowElement.createCell(7).setCellValue("Time Completed");
            rowElement.createCell(8).setCellValue("Start Task");
            rowCount++;

            for (TableRows tableRow : tableRows) {
                rowElement = worksheet.createRow(rowCount);
                String[] rowElements = tableRow.toString().split(",");
                for (int cellNum = 0; cellNum < rowElements.length; cellNum++) {
                    rowElement.createCell(cellNum).setCellValue(rowElements[cellNum]);
                }
                rowCount++;
            }
                /*
                try {
                    String tableId = tableName.split(":|\\)")[1].strip();
                    FileWriter writer = new FileWriter(employeeFolder.resolve(tableId + ".csv").toFile());
                    writer.write("Complete,EE Task ID,Task ID,Task Description,Task Type,Task For,Completed By,Time Completed,Start Task\n");
                    for (TableRows checklistRow : tableRows) { //**
                        writer.write(checklistRow.toString());
                    }
                    writer.close();
                } catch (IOException e) {
                    logger.fatal("An error occurred creating the FileWriter");
                }
                 */

            // Click the cancel button
            Waits.waitUntilClickable(driver, By.xpath("//a[@id='butcancel']"));
        }
        // We are finished collecting the data from tables for an employee


        try {
            FileOutputStream outputStream = new FileOutputStream(employeesFolder.resolve(eeCode).resolve(eeCode + "_checklists.xlsx").toString());
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.fatal("An error occurred while writing excel files.");
        }
    }
}
