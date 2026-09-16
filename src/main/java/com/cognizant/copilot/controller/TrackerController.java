package com.cognizant.copilot.controller;

import com.cognizant.copilot.model.TrackerResult;
import com.cognizant.copilot.model.UserInfo;
import com.cognizant.copilot.model.UsageEntry;
import com.cognizant.copilot.service.EmailService;
import com.cognizant.copilot.service.ReportGenerationService;
import com.cognizant.copilot.service.TrackerAnalysisService;
import com.cognizant.copilot.service.UserReadService;
import com.cognizant.copilot.service.UsageReadService;
import com.cognizant.copilot.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Copilot Tracker Monitor Web API
 */
@RestController
@RequestMapping("/api/tracker")
public class TrackerController {

    private static final Logger logger = LoggerFactory.getLogger(TrackerController.class);

    private TrackerResult lastResult = null;

    /**
     * Analyze tracker data for a given date range
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeTracker(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        try {
            // Default to last 5 days if not provided
            if (toDate == null) {
                toDate = LocalDate.now();
            }
            if (fromDate == null) {
                fromDate = toDate.minusDays(4);
            }

            logger.info("Running tracker analysis for {} to {}", fromDate, toDate);

            // Initialize services
            UserReadService userReadService = new UserReadService();
            UsageReadService usageReadService = new UsageReadService();
            TrackerAnalysisService analysisService = new TrackerAnalysisService();
            AppConfig config = AppConfig.getInstance();
            ReportGenerationService reportService = new ReportGenerationService(
                    config.getString("chart.output.directory", "dashboard-charts"),
                    config.getInt("chart.resource.top.count", 15)
            );

            // Read data
            List<UserInfo> users = userReadService.readUserList();
            List<UsageEntry> usageEntries = usageReadService.readUsageEntries();

            // Analyze
            TrackerResult result = analysisService.analyzeTracker(users, usageEntries, fromDate, toDate);

            // Generate charts
            reportService.generateDashboardCharts(result);

            // Store result for later retrieval
            lastResult = result;

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Tracker analysis completed");
            response.put("fromDate", fromDate);
            response.put("toDate", toDate);
            response.put("updatedCount", result.updatedUsers.size());
            response.put("pendingCount", result.pendingUsers.size());
            response.put("defaultersCount", result.fiveDayDefaulters.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error analyzing tracker", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get dashboard data
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        if (lastResult == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "No analysis data available. Run /api/tracker/analyze first.");
            return ResponseEntity.status(404).body(error);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("status", "success");
        dashboard.put("updatedUsers", lastResult.updatedUsers);
        dashboard.put("pendingUsers", lastResult.pendingUsers);
        dashboard.put("fiveDayDefaulters", lastResult.fiveDayDefaulters);
        dashboard.put("teamMetrics", lastResult.teamMetrics);
        dashboard.put("resourceMetrics", lastResult.resourceMetrics);

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Serve chart image
     */
    @GetMapping("/charts/{chartName}")
    public ResponseEntity<Resource> getChart(@PathVariable String chartName) {
        try {
            AppConfig config = AppConfig.getInstance();
            String chartDir = config.getString("chart.output.directory", "dashboard-charts");
            File chartFile = new File(chartDir, chartName + ".png");

            if (!chartFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(chartFile);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + chartName + ".png\"")
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error retrieving chart", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", LocalDate.now().toString());
        return ResponseEntity.ok(response);
    }
}
