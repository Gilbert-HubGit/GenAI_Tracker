package com.cognizant.copilot.service;

import com.cognizant.copilot.config.AppConfig;
import com.cognizant.copilot.model.UserInfo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for reading user information from Excel file.
 */
public class UserReadService {

    private static final Logger logger = LoggerFactory.getLogger(UserReadService.class);

    private final String excelPath;
    private final String userListSheet;

    public UserReadService() {
        this.excelPath = AppConfig.getInstance().getString("excel.file.path");
        this.userListSheet = AppConfig.getInstance().getString("excel.sheet.users");
    }

    /**
     * Read user list from Excel file
     */
    public List<UserInfo> readUserList() throws Exception {
        List<UserInfo> users = new ArrayList<>();

        logger.info("Reading user list from: {}", excelPath);

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(userListSheet);

            if (sheet == null) {
                logger.error("Sheet not found: {}", userListSheet);
                throw new RuntimeException("Sheet not found: " + userListSheet);
            }

            Map<String, Integer> headers = getHeaderMap(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String associateId = ExcelUtils.getCellValue(row, headers.get("Associate ID"));
                String name = ExcelUtils.getCellValue(row, headers.get("Name"));
                String teamLead = ExcelUtils.getCellValue(row, headers.get("Team Lead"));
                String managerPoc = ExcelUtils.getCellValue(row, headers.get("Manager POC"));
                String teamName = ExcelUtils.getCellValue(row, headers.get("Team Name"));

                if (StringUtils.isBlank(name)) {
                    continue;
                }

                UserInfo user = new UserInfo();
                user.associateId = StringUtils.cleanNumberId(associateId);
                user.name = name.trim();
                user.normalizedName = NameMappingUtils.applyNameMapping(name);
                user.teamLead = teamLead;
                user.managerPoc = managerPoc;
                user.teamName = teamName;
                user.email = StringUtils.generateDummyEmail(name);
                user.teamLeadEmail = StringUtils.generateDummyEmail(teamLead);
                user.managerEmail = StringUtils.generateDummyEmail(managerPoc);

                users.add(user);
            }
        }

        logger.info("Read {} users from Excel", users.size());
        return users;
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
