package com.cognizant.copilot.service;

import com.cognizant.copilot.config.AppConfig;
import com.cognizant.copilot.model.ResourceMetric;
import com.cognizant.copilot.model.TeamMetric;
import com.cognizant.copilot.model.TrackerResult;
import com.cognizant.copilot.model.UserInfo;
import com.cognizant.copilot.model.UsageEntry;
import com.cognizant.copilot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for analyzing tracker data.
 */
public class TrackerAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(TrackerAnalysisService.class);

    private final int continuousMissingDays;

    public TrackerAnalysisService() {
        this.continuousMissingDays = AppConfig.getInstance().getInt("tracker.continuous.missing.days", 5);
    }

    /**
     * Analyze tracker data and generate results
     */
    public TrackerResult analyzeTracker(
            List<UserInfo> users,
            List<UsageEntry> entries,
            LocalDate fromDate,
            LocalDate toDate) {

        logger.info("Analyzing tracker data from {} to {}", fromDate, toDate);

        TrackerResult result = new TrackerResult();

        List<UsageEntry> filteredEntries = entries.stream()
                .filter(e -> !e.entryDate.isBefore(fromDate) && !e.entryDate.isAfter(toDate))
                .collect(Collectors.toList());

        result.filteredUsageEntries = filteredEntries;

        Map<String, List<UsageEntry>> usageByUserForDateRange = filteredEntries.stream()
                .collect(Collectors.groupingBy(e -> e.normalizedResourceName));

        Map<String, List<UsageEntry>> allUsageByUser = entries.stream()
                .collect(Collectors.groupingBy(e -> e.normalizedResourceName));

        for (UserInfo user : users) {
            List<UsageEntry> userEntries =
                    usageByUserForDateRange.getOrDefault(user.normalizedName, new ArrayList<>());

            if (userEntries.isEmpty()) {
                result.pendingUsers.add(user);
            } else {
                result.updatedUsers.add(user);
            }

            if (isMissingContinuously(user, allUsageByUser, toDate)) {
                result.fiveDayDefaulters.add(user);
            }
        }

        buildTeamDashboard(result, filteredEntries);
        buildResourceDashboard(result, filteredEntries);

        logger.info("Analysis complete - Updated: {}, Pending: {}, Defaulters: {}",
                result.updatedUsers.size(), result.pendingUsers.size(), result.fiveDayDefaulters.size());

        return result;
    }

    /**
     * Check if user is missing entries continuously
     */
    private boolean isMissingContinuously(
            UserInfo user,
            Map<String, List<UsageEntry>> allUsageByUser,
            LocalDate toDate) {

        List<UsageEntry> entries =
                allUsageByUser.getOrDefault(user.normalizedName, new ArrayList<>());

        Set<LocalDate> entryDates = entries.stream()
                .map(e -> e.entryDate)
                .collect(Collectors.toSet());

        for (int i = 0; i < continuousMissingDays; i++) {
            LocalDate checkDate = toDate.minusDays(i);

            if (entryDates.contains(checkDate)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Build team dashboard metrics
     */
    private void buildTeamDashboard(TrackerResult result, List<UsageEntry> entries) {
        Map<String, TeamMetric> map = new HashMap<>();

        for (UsageEntry entry : entries) {
            String team = StringUtils.isBlank(entry.teamName) ? "UNKNOWN" : entry.teamName.trim();

            TeamMetric metric = map.computeIfAbsent(team, TeamMetric::new);

            metric.totalCopilotHours += entry.copilotHours;
            metric.totalWithoutCopilotHours += entry.withoutCopilotHours;
            metric.totalBenefitHours += entry.benefitHours;
            metric.entryCount++;
        }

        result.teamMetrics = map.values().stream()
                .sorted(Comparator.comparingDouble((TeamMetric t) -> t.totalBenefitHours).reversed())
                .collect(Collectors.toList());

        logger.debug("Built team dashboard with {} teams", result.teamMetrics.size());
    }

    /**
     * Build resource dashboard metrics
     */
    private void buildResourceDashboard(TrackerResult result, List<UsageEntry> entries) {
        Map<String, ResourceMetric> map = new HashMap<>();

        for (UsageEntry entry : entries) {
            String name = StringUtils.isBlank(entry.resourceName) ? "UNKNOWN" : entry.resourceName.trim();

            ResourceMetric metric = map.computeIfAbsent(name, ResourceMetric::new);

            metric.teamName = entry.teamName;
            metric.totalCopilotHours += entry.copilotHours;
            metric.totalWithoutCopilotHours += entry.withoutCopilotHours;
            metric.totalBenefitHours += entry.benefitHours;
            metric.entryCount++;
        }

        result.resourceMetrics = map.values().stream()
                .sorted(Comparator.comparingDouble((ResourceMetric r) -> r.totalBenefitHours).reversed())
                .collect(Collectors.toList());

        logger.debug("Built resource dashboard with {} resources", result.resourceMetrics.size());
    }
}
