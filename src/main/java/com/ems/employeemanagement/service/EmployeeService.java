package com.ems.employeemanagement.service;

import com.ems.employeemanagement.dto.DashboardStatsDto;
import com.ems.employeemanagement.dto.EmployeeRequestDto;
import com.ems.employeemanagement.dto.EmployeeResponseDto;
import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto request);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request);

    EmployeeResponseDto getEmployeeById(Long id);

    EmployeeResponseDto toggleEmployeeStatus(Long id);

    void deleteEmployee(Long id);

    Page<EmployeeResponseDto> searchAndFilterEmployees(String keyword, Department department, EmployeeStatus status, Pageable pageable);

    List<EmployeeResponseDto> getAllEmployees();

    List<EmployeeResponseDto> searchEmployees(String keyword);

    List<EmployeeResponseDto> getEmployeesByDepartment(Department department);

    DashboardStatsDto getDashboardStats();
}
