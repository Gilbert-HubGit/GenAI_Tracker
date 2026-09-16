package com.cognizant.copilot.model;

/**
 * Resource-wise metrics data model.
 */
public class ResourceMetric {
    public String resourceName;
    public String teamName;
    public int entryCount;
    public double totalCopilotHours;
    public double totalWithoutCopilotHours;
    public double totalBenefitHours;

    public ResourceMetric() {
    }

    public ResourceMetric(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return "ResourceMetric{" +
                "resourceName='" + resourceName + '\'' +
                ", teamName='" + teamName + '\'' +
                ", totalBenefitHours=" + totalBenefitHours +
                ", entryCount=" + entryCount +
                '}';
    }
}
