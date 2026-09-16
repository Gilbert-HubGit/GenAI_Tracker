package com.cognizant.copilot.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Excel utility methods for reading and parsing Excel cells.
 */
public class ExcelUtils {

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    /**
     * Get cell value as string
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return DATA_FORMATTER.formatCellValue(cell).trim();
    }

    /**
     * Get cell value from row by column index
     */
    public static String getCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null || columnIndex < 0) {
            return "";
        }

        Cell cell = row.getCell(columnIndex);
        return getCellValue(cell);
    }

    /**
     * Get date cell value
     */
    public static LocalDate getDateCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null || columnIndex < 0) {
            return null;
        }

        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            String value = getCellValue(cell);

            if (StringUtils.isBlank(value)) {
                return null;
            }

            return LocalDate.parse(value, DateFormatUtils.INPUT_DATE_FORMATTER);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get numeric cell value
     */
    public static double getNumericCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null || columnIndex < 0) {
            return 0.0;
        }

        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return 0.0;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }

            String value = getCellValue(cell);

            if (StringUtils.isBlank(value)) {
                return 0.0;
            }

            return Double.parseDouble(value);

        } catch (Exception e) {
            return 0.0;
        }
    }
}
