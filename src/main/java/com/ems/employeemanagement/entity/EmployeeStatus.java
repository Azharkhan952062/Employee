package com.ems.employeemanagement.entity;

public enum EmployeeStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String displayName;

    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EmployeeStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return ACTIVE;
        }
        for (EmployeeStatus status : EmployeeStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim()) || 
                status.displayName.equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}
