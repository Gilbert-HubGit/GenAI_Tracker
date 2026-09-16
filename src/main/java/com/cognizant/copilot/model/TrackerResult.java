package com.cognizant.copilot.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracker analysis result data model.
 */
public class TrackerResult {
    public List<UserInfo> updatedUsers = new ArrayList<>();
    public List<UserInfo> pendingUsers = new ArrayList<>();
    public List<UserInfo> fiveDayDefaulters = new ArrayList<>();
    public List<TeamMetric> teamMetrics = new ArrayList<>();
    public List<ResourceMetric> resourceMetrics = new ArrayList<>();
    public List<UsageEntry> filteredUsageEntries = new ArrayList<>();

    @Override
    public String toString() {
        return "TrackerResult{" +
                "updatedUsers=" + updatedUsers.size() +
                ", pendingUsers=" + pendingUsers.size() +
                ", fiveDayDefaulters=" + fiveDayDefaulters.size() +
                '}';
    }
}
