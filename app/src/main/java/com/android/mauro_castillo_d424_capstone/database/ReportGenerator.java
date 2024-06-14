package com.android.mauro_castillo_d424_capstone.database;

import com.android.mauro_castillo_d424_capstone.entities.Vacation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    public void generateExcelReport(List<Vacation> vacations, String filename) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Vacations");

        // create title row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Vacation Report");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // create date-time stamp row
        Row dateTimeRow = sheet.createRow(1);
        Cell dateTimeCell = dateTimeRow.createCell(0);
        dateTimeCell.setCellValue("Generated on: " + new java.util.Date());

        // create header row
        Row headerRow = sheet.createRow(2);
        String[] headers = {"Vacation ID", "Destination", "Hotel", "Start Date", "End Date"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // populate data rows
        int rowNum = 3;
        for (Vacation vacation : vacations) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(vacation.getVacationId());
            row.createCell(1).setCellValue(vacation.getVacationName());
            row.createCell(2).setCellValue(vacation.getHotelName());
            row.createCell(3).setCellValue(vacation.getStartDate());
            row.createCell(4).setCellValue(vacation.getEndDate());
        }

        // write the output to a file
        try {
            File file = new File(filename);
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}















