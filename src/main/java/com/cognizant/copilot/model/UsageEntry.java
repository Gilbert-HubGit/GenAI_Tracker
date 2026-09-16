package com.cognizant.copilot.model;

import java.time.LocalDate;

/**
 * Usage entry data model.
 */
public class UsageEntry {
    public String teamName;
    public String associateId;
    public String resourceName;
    public String normalizedResourceName;
    public LocalDate entryDate;
    public double copilotHours;
    public double withoutCopilotHours;
    public double benefitHours;

    @Override
    public String toString() {
        return "UsageEntry{" +
                "resourceName='" + resourceName + '\'' +
                ", entryDate=" + entryDate +
                ", benefitHours=" + benefitHours +
                '}';
    }
}
