package com.ems.employeemanagement.entity;

public enum Department {
    ENGINEERING("Engineering"),
    HUMAN_RESOURCES("Human Resources"),
    MARKETING("Marketing"),
    FINANCE("Finance"),
    SALES("Sales"),
    OPERATIONS("Operations"),
    PRODUCT("Product"),
    LEGAL("Legal");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Department fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (Department dept : Department.values()) {
            if (dept.name().equalsIgnoreCase(text.trim()) || 
                dept.displayName.equalsIgnoreCase(text.trim()) ||
                dept.name().replace("_", " ").equalsIgnoreCase(text.trim())) {
                return dept;
            }
        }
        throw new IllegalArgumentException("Unknown department: " + text);
    }
}
