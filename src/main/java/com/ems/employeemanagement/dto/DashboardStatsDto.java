package com.ems.employeemanagement.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDto {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private BigDecimal averageSalary;
    private BigDecimal totalMonthlyPayroll;
    private BigDecimal highestSalary;
    private List<DepartmentStatDto> departmentStats = new ArrayList<>();
    private List<EmployeeResponseDto> recentHires = new ArrayList<>();

    public DashboardStatsDto() {
        this.averageSalary = BigDecimal.ZERO;
        this.totalMonthlyPayroll = BigDecimal.ZERO;
        this.highestSalary = BigDecimal.ZERO;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public long getInactiveEmployees() {
        return inactiveEmployees;
    }

    public void setInactiveEmployees(long inactiveEmployees) {
        this.inactiveEmployees = inactiveEmployees;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    public BigDecimal getTotalMonthlyPayroll() {
        return totalMonthlyPayroll;
    }

    public void setTotalMonthlyPayroll(BigDecimal totalMonthlyPayroll) {
        this.totalMonthlyPayroll = totalMonthlyPayroll;
    }

    public BigDecimal getHighestSalary() {
        return highestSalary;
    }

    public void setHighestSalary(BigDecimal highestSalary) {
        this.highestSalary = highestSalary;
    }

    public List<DepartmentStatDto> getDepartmentStats() {
        return departmentStats;
    }

    public void setDepartmentStats(List<DepartmentStatDto> departmentStats) {
        this.departmentStats = departmentStats;
    }

    public List<EmployeeResponseDto> getRecentHires() {
        return recentHires;
    }

    public void setRecentHires(List<EmployeeResponseDto> recentHires) {
        this.recentHires = recentHires;
    }
}
