package com.cognizant.copilot.service;

import com.cognizant.copilot.model.ResourceMetric;
import com.cognizant.copilot.model.TeamMetric;
import com.cognizant.copilot.model.TrackerResult;
import com.cognizant.copilot.model.UserInfo;
import com.cognizant.copilot.model.UsageEntry;
import com.cognizant.copilot.util.DateFormatUtils;
import com.cognizant.copilot.util.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.StandardChartTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service for generating charts and reports.
 */
public class ReportGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerationService.class);

    private final String chartOutputDir;
    private final int resourceTopCount;

    public ReportGenerationService(String chartOutputDir, int resourceTopCount) {
        this.chartOutputDir = chartOutputDir;
        this.resourceTopCount = resourceTopCount;
    }

    /**
     * Generate all dashboard charts
     */
    public void generateDashboardCharts(TrackerResult result) {
        try {
            File chartDir = new File(chartOutputDir);

            if (!chartDir.exists() && !chartDir.mkdirs()) {
                logger.error("Failed to create chart directory: {}", chartOutputDir);
                throw new RuntimeException("Failed to create chart directory");
            }

            generateTeamBenefitChart(result, chartDir);
            generateBenefitTrendLineChart(result, chartDir);

            logger.info("Dashboard charts generated successfully in: {}", chartOutputDir);
            printChartGenerationSummary();

        } catch (Exception e) {
            logger.error("Error while generating dashboard charts", e);
        }
    }

    /**
     * Generate team benefit chart (Pie Chart)
     */
    private void generateTeamBenefitChart(TrackerResult result, File chartDir) throws Exception {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (TeamMetric metric : result.teamMetrics) {
            dataset.setValue(metric.teamName, metric.totalBenefitHours);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Team-wise GenAI Benefit Hours Distribution",
                dataset,
                true,
                true,
                false
        );

        // Customize pie chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(240, 247, 250));
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        plot.setIgnoreZeroValues(true);
        plot.setSectionOutlinesVisible(true);

        chart.getTitle().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        chart.getLegend().setItemFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));

        ChartUtils.saveChartAsPNG(
                new File(chartDir, "team-benefit-chart.png"),
                chart,
                1000,
                700
        );

        logger.debug("Generated team-benefit-chart.png");
    }

    /**
     * Generate team effort comparison chart (Pie Chart - Total Benefit)
     */
    private void generateTeamEffortComparisonChart(TrackerResult result, File chartDir) throws Exception {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (TeamMetric metric : result.teamMetrics) {
            dataset.setValue(metric.teamName + " (With Copilot)", metric.totalCopilotHours);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Team-wise Copilot Hours Distribution",
                dataset,
                true,
                true,
                false
        );

        // Customize pie chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(245, 247, 250));
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        plot.setIgnoreZeroValues(true);
        plot.setSectionOutlinesVisible(true);

        chart.getTitle().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        chart.getLegend().setItemFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

        ChartUtils.saveChartAsPNG(
                new File(chartDir, "team-effort-comparison-chart.png"),
                chart,
                1000,
                700
        );

        logger.debug("Generated team-effort-comparison-chart.png");
    }

    /**
     * Generate resource benefit chart (top N resources) - Pie Chart
     */
    private void generateResourceBenefitChart(TrackerResult result, File chartDir) throws Exception {
        DefaultPieDataset dataset = new DefaultPieDataset();

        int count = 0;
        for (ResourceMetric metric : result.resourceMetrics) {
            if (count >= resourceTopCount) {
                break;
            }

            dataset.setValue(metric.resourceName, metric.totalBenefitHours);
            count++;
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Top Resources by GenAI Benefit Hours Distribution",
                dataset,
                true,
                true,
                false
        );

        // Customize pie chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(240, 247, 250));
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        plot.setIgnoreZeroValues(true);
        plot.setSectionOutlinesVisible(true);

        chart.getTitle().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        chart.getLegend().setItemFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

        ChartUtils.saveChartAsPNG(
                new File(chartDir, "resource-benefit-chart.png"),
                chart,
                1000,
                700
        );

        logger.debug("Generated resource-benefit-chart.png");
    }

    /**
     * Generate benefit trend line chart
     */
    private void generateBenefitTrendLineChart(TrackerResult result, File chartDir) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<LocalDate, Double> benefitByDate = new TreeMap<>();

        for (UsageEntry entry : result.filteredUsageEntries) {
            benefitByDate.merge(entry.entryDate, entry.benefitHours, Double::sum);
        }

        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("MM/dd");

        for (Map.Entry<LocalDate, Double> entry : benefitByDate.entrySet()) {
            dataset.addValue(entry.getValue(), "Total Benefit Hours", entry.getKey().format(displayFormat));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "GenAI Benefit Trend Over Time",
                "Date",
                "Benefit Hours",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartUtils.saveChartAsPNG(
                new File(chartDir, "benefit-trend-line-chart.png"),
                chart,
                1100,
                650
        );

        logger.debug("Generated benefit-trend-line-chart.png");
    }

    /**
     * Generate team-wise benefit trend line chart
     */
    private void generateTeamWiseBenefitTrendLineChart(TrackerResult result, File chartDir) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Map<LocalDate, Double>> teamDateBenefitMap = new TreeMap<>();

        for (UsageEntry entry : result.filteredUsageEntries) {
            String teamName = StringUtils.isBlank(entry.teamName) ? "UNKNOWN" : entry.teamName.trim();

            teamDateBenefitMap
                    .computeIfAbsent(teamName, k -> new TreeMap<>())
                    .merge(entry.entryDate, entry.benefitHours, Double::sum);
        }

        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("MM/dd");

        for (Map.Entry<String, Map<LocalDate, Double>> teamEntry : teamDateBenefitMap.entrySet()) {
            String teamName = teamEntry.getKey();

            for (Map.Entry<LocalDate, Double> dateEntry : teamEntry.getValue().entrySet()) {
                dataset.addValue(
                        dateEntry.getValue(),
                        teamName,
                        dateEntry.getKey().format(displayFormat)
                );
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Team-wise GenAI Benefit Trend Over Time",
                "Date",
                "Benefit Hours",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartUtils.saveChartAsPNG(
                new File(chartDir, "team-wise-benefit-trend-line-chart.png"),
                chart,
                1300,
                700
        );

        logger.debug("Generated team-wise-benefit-trend-line-chart.png");
    }

    /**
     * Print missing report
     */
    public void printMissingReport(TrackerResult result, LocalDate fromDate, LocalDate toDate) {
        System.out.println("\n======================================");
        System.out.println("COPILOT TRACKER MISSING ENTRY REPORT");
        System.out.println("Date Range: " + fromDate + " to " + toDate);
        System.out.println("======================================");

        System.out.println("\nUPDATED USERS (" + result.updatedUsers.size() + ")");
        if (result.updatedUsers.isEmpty()) {
            System.out.println("No updated users found.");
        } else {
            for (UserInfo user : result.updatedUsers) {
                System.out.println("[OK] " + user.name + " | Team: " + user.teamName);
            }
        }

        System.out.println("\nPENDING USERS (" + result.pendingUsers.size() + ")");
        if (result.pendingUsers.isEmpty()) {
            System.out.println("No pending users found.");
        } else {
            for (UserInfo user : result.pendingUsers) {
                System.out.println("[PENDING] " + user.name
                        + " | Team: " + user.teamName
                        + " | Lead: " + user.teamLead);
            }
        }

        System.out.println("\nCONTINUOUS 5 DAY DEFAULTERS (" + result.fiveDayDefaulters.size() + ")");
        if (result.fiveDayDefaulters.isEmpty()) {
            System.out.println("No continuous 5-day defaulters found.");
        } else {
            for (UserInfo user : result.fiveDayDefaulters) {
                System.out.println("[ESCALATE] " + user.name
                        + " | Team: " + user.teamName
                        + " | Manager: " + user.managerPoc);
            }
        }
    }

    /**
     * Print team dashboard
     */
    public void printTeamDashboard(TrackerResult result) {
        System.out.println("\n======================================");
        System.out.println("TEAM-WISE DASHBOARD");
        System.out.println("Sorted by Benefit Hours High to Low");
        System.out.println("======================================");

        System.out.printf("%-25s %-12s %-15s %-15s %-10s%n",
                "Team", "Entries", "Copilot Hrs", "Without Hrs", "Benefit");

        for (TeamMetric metric : result.teamMetrics) {
            System.out.printf("%-25s %-12d %-15.2f %-15.2f %-10.2f%n",
                    metric.teamName,
                    metric.entryCount,
                    metric.totalCopilotHours,
                    metric.totalWithoutCopilotHours,
                    metric.totalBenefitHours);
        }
    }

    /**
     * Print resource dashboard
     */
    public void printResourceDashboard(TrackerResult result) {
        System.out.println("\n======================================");
        System.out.println("RESOURCE-WISE DASHBOARD");
        System.out.println("Sorted by Benefit Hours High to Low");
        System.out.println("======================================");

        System.out.printf("%-30s %-25s %-12s %-15s %-15s %-10s%n",
                "Resource", "Team", "Entries", "Copilot Hrs", "Without Hrs", "Benefit");

        for (ResourceMetric metric : result.resourceMetrics) {
            System.out.printf("%-30s %-25s %-12d %-15.2f %-15.2f %-10.2f%n",
                    metric.resourceName,
                    metric.teamName,
                    metric.entryCount,
                    metric.totalCopilotHours,
                    metric.totalWithoutCopilotHours,
                    metric.totalBenefitHours);
        }
    }

    /**
     * Print chart generation summary
     */
    private void printChartGenerationSummary() {
        System.out.println("\n======================================");
        System.out.println("DASHBOARD CHARTS GENERATED");
        System.out.println("======================================");
        System.out.println("1. " + chartOutputDir + "/team-benefit-chart.png");
        System.out.println("2. " + chartOutputDir + "/benefit-trend-line-chart.png");
    }
}
