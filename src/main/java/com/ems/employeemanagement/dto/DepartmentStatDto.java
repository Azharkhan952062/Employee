package com.ems.employeemanagement.dto;

import com.ems.employeemanagement.entity.Department;

import java.math.BigDecimal;

public class DepartmentStatDto {

    private Department department;
    private String departmentDisplayName;
    private long employeeCount;
    private BigDecimal averageSalary;
    private BigDecimal totalSalary;
    private double percentage;

    public DepartmentStatDto() {
    }

    public DepartmentStatDto(Department department, long employeeCount, BigDecimal averageSalary, BigDecimal totalSalary, double percentage) {
        this.department = department;
        this.departmentDisplayName = department != null ? department.getDisplayName() : "Unknown";
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary != null ? averageSalary : BigDecimal.ZERO;
        this.totalSalary = totalSalary != null ? totalSalary : BigDecimal.ZERO;
        this.percentage = percentage;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
        this.departmentDisplayName = department != null ? department.getDisplayName() : "Unknown";
    }

    public String getDepartmentDisplayName() {
        return departmentDisplayName;
    }

    public void setDepartmentDisplayName(String departmentDisplayName) {
        this.departmentDisplayName = departmentDisplayName;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(long employeeCount) {
        this.employeeCount = employeeCount;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
