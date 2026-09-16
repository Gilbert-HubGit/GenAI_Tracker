package com.cognizant.copilot.service;

import com.cognizant.copilot.config.AppConfig;
import com.cognizant.copilot.model.UsageEntry;
import com.cognizant.copilot.util.ExcelUtils;
import com.cognizant.copilot.util.NameMappingUtils;
import com.cognizant.copilot.util.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for reading usage entries from Excel file.
 */
public class UsageReadService {

    private static final Logger logger = LoggerFactory.getLogger(UsageReadService.class);

    private final String excelPath;
    private final String usageSheet;

    public UsageReadService() {
        this.excelPath = AppConfig.getInstance().getString("excel.file.path");
        this.usageSheet = AppConfig.getInstance().getString("excel.sheet.usage");
    }

    /**
     * Read usage entries from Excel file
     */
    public List<UsageEntry> readUsageEntries() throws Exception {
        List<UsageEntry> entries = new ArrayList<>();

        logger.info("Reading usage entries from: {}", excelPath);

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(usageSheet);

            if (sheet == null) {
                logger.error("Sheet not found: {}", usageSheet);
                throw new RuntimeException("Sheet not found: " + usageSheet);
            }

            Map<String, Integer> headers = getHeaderMap(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String teamName = ExcelUtils.getCellValue(row, headers.get("Team Name"));
                String associateId = ExcelUtils.getCellValue(row, headers.get("Cognizant ID"));
                String resourceName = ExcelUtils.getCellValue(row, headers.get("Resource Name"));

                LocalDate entryDate = ExcelUtils.getDateCellValue(row, headers.get("Date"));

                double copilotHours = ExcelUtils.getNumericCellValue(row, headers.get("Efforts spent with CoPilot (hrs)"));
                double withoutCopilotHours = ExcelUtils.getNumericCellValue(row, headers.get("Efforts spent without CoPilot (hrs)"));
                double benefitHours = ExcelUtils.getNumericCellValue(row, headers.get("Benefit (hrs.)"));

                if (entryDate == null || StringUtils.isBlank(resourceName)) {
                    continue;
                }

                UsageEntry entry = new UsageEntry();
                entry.teamName = teamName;
                entry.associateId = StringUtils.cleanNumberId(associateId);
                entry.resourceName = resourceName.trim();
                entry.normalizedResourceName = NameMappingUtils.applyNameMapping(resourceName);
                entry.entryDate = entryDate;
                entry.copilotHours = copilotHours;
                entry.withoutCopilotHours = withoutCopilotHours;
                entry.benefitHours = benefitHours;

                entries.add(entry);
            }
        }

        logger.info("Read {} usage entries from Excel", entries.size());
        return entries;
    }

    /**
     * Get header map from row
     */
    private Map<String, Integer> getHeaderMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();

        if (headerRow == null) {
            return map;
        }

        headerRow.forEach(cell -> {
            String header = ExcelUtils.getCellValue(cell);
            if (!StringUtils.isBlank(header)) {
                header = StringUtils.cleanHeader(header);
                map.put(header, cell.getColumnIndex());
            }
        });

        return map;
    }
}
