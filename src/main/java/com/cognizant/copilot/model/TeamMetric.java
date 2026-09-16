package com.cognizant.copilot.model;

/**
 * Team-wise metrics data model.
 */
public class TeamMetric {
    public String teamName;
    public int entryCount;
    public double totalCopilotHours;
    public double totalWithoutCopilotHours;
    public double totalBenefitHours;

    public TeamMetric() {
    }

    public TeamMetric(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return "TeamMetric{" +
                "teamName='" + teamName + '\'' +
                ", totalBenefitHours=" + totalBenefitHours +
                ", entryCount=" + entryCount +
                '}';
    }
}
