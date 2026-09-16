package com.cognizant.copilot;

import com.cognizant.copilot.config.AppConfig;
import com.cognizant.copilot.model.TrackerResult;
import com.cognizant.copilot.model.UserInfo;
import com.cognizant.copilot.model.UsageEntry;
import com.cognizant.copilot.service.EmailService;
import com.cognizant.copilot.service.ReportGenerationService;
import com.cognizant.copilot.service.TrackerAnalysisService;
import com.cognizant.copilot.service.UserReadService;
import com.cognizant.copilot.service.UsageReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Main application class for Copilot Tracker Monitor.
 * Orchestrates reading Excel data, analyzing usage, generating reports, and sending emails.
 */
public class CopilotTrackerMonitor {

    private static final Logger logger = LoggerFactory.getLogger(CopilotTrackerMonitor.class);

    /**
     * Main entry point for the application
     * 
     * Usage:
     * - mvn clean compile exec:java (uses last 5 days by default)
     * - mvn clean compile exec:java -Dexec.args="08/05/2026 08/11/2026" (custom date range)
     */
    public static void main(String[] args) {
        LocalDate fromDate;
        LocalDate toDate;

        // Parse date range from arguments or use default
        if (args.length >= 2) {
            try {
                fromDate = LocalDate.parse(args[0], com.cognizant.copilot.util.DateFormatUtils.getInputFormatter());
                toDate = LocalDate.parse(args[1], com.cognizant.copilot.util.DateFormatUtils.getInputFormatter());
            } catch (Exception e) {
                logger.error("Invalid date format. Expected: MM/dd/yyyy", e);
                System.exit(1);
                return;
            }
        } else {
            // Default: last 5 days including today
            toDate = LocalDate.now();
            fromDate = toDate.minusDays(4);
        }

        try {
            System.out.println("======================================");
            System.out.println("GENAI COPILOT TRACKER MONITOR STARTED");
            System.out.println("======================================");
            System.out.println("Date Range: " + fromDate + " to " + toDate);

            logger.info("Starting Copilot Tracker Monitor");

            // Initialize services
            UserReadService userReadService = new UserReadService();
            UsageReadService usageReadService = new UsageReadService();
            TrackerAnalysisService analysisService = new TrackerAnalysisService();
            AppConfig config = AppConfig.getInstance();
            ReportGenerationService reportService = new ReportGenerationService(
                    config.getString("chart.output.directory", "dashboard-charts"),
                    config.getInt("chart.resource.top.count", 15)
            );
            EmailService emailService = new EmailService();

            // Read data
            List<UserInfo> users = userReadService.readUserList();
            List<UsageEntry> usageEntries = usageReadService.readUsageEntries();

            // Analyze
            TrackerResult result = analysisService.analyzeTracker(users, usageEntries, fromDate, toDate);

            // Generate reports
            reportService.printMissingReport(result, fromDate, toDate);
            reportService.printTeamDashboard(result);
            reportService.printResourceDashboard(result);

            // Generate charts
            reportService.generateDashboardCharts(result);

            // Send emails
            emailService.sendReminderAndEscalationMails(result, fromDate, toDate);

            System.out.println("\n======================================");
            System.out.println("GENAI COPILOT TRACKER MONITOR COMPLETED");
            System.out.println("======================================");

            logger.info("Copilot Tracker Monitor completed successfully");

        } catch (Exception e) {
            logger.error("Error occurred while processing tracker", e);
            System.out.println("Error occurred while processing tracker");
            e.printStackTrace();
            System.exit(1);
        }
    }
}