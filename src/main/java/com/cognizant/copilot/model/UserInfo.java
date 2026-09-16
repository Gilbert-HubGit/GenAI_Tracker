package com.cognizant.copilot.model;

/**
 * User information data model.
 */
public class UserInfo {
    public String associateId;
    public String name;
    public String normalizedName;
    public String teamLead;
    public String managerPoc;
    public String teamName;
    public String email;
    public String teamLeadEmail;
    public String managerEmail;

    @Override
    public String toString() {
        return "UserInfo{" +
                "associateId='" + associateId + '\'' +
                ", name='" + name + '\'' +
                ", teamName='" + teamName + '\'' +
                '}';
    }
}
