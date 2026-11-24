package com.ems.employeemanagement.service.impl;

import com.ems.employeemanagement.dto.DashboardStatsDto;
import com.ems.employeemanagement.dto.DepartmentStatDto;
import com.ems.employeemanagement.dto.EmployeeRequestDto;
import com.ems.employeemanagement.dto.EmployeeResponseDto;
import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.Employee;
import com.ems.employeemanagement.entity.EmployeeStatus;
import com.ems.employeemanagement.exception.DuplicateResourceException;
import com.ems.employeemanagement.exception.ResourceNotFoundException;
import com.ems.employeemanagement.repository.EmployeeRepository;
import com.ems.employeemanagement.repository.EmployeeSpecification;
import com.ems.employeemanagement.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
        log.info("Creating new employee with email: {}", request.getEmail());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }

        Employee employee = new Employee();
        mapDtoToEntity(request, employee);

        Employee saved = employeeRepository.save(employee);
        log.info("Successfully created employee with id: {}", saved.getId());
        return EmployeeResponseDto.fromEntity(saved);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request) {
        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        if (employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }

        mapDtoToEntity(request, employee);
        Employee updated = employeeRepository.save(employee);
        log.info("Successfully updated employee with id: {}", updated.getId());
        return EmployeeResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeResponseDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    @Override
    public EmployeeResponseDto toggleEmployeeStatus(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        EmployeeStatus current = employee.getStatus();
        EmployeeStatus next = (current == EmployeeStatus.ACTIVE) ? EmployeeStatus.INACTIVE : EmployeeStatus.ACTIVE;
        employee.setStatus(next);

        Employee saved = employeeRepository.save(employee);
        log.info("Toggled status for employee {} from {} to {}", id, current, next);
        return EmployeeResponseDto.fromEntity(saved);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeRepository.deleteById(id);
        log.info("Successfully deleted employee with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> searchAndFilterEmployees(String keyword, Department department, EmployeeStatus status, Pageable pageable) {
        Specification<Employee> spec = EmployeeSpecification.filter(keyword, department, status);
        return employeeRepository.findAll(spec, pageable).map(EmployeeResponseDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> searchEmployees(String keyword) {
        log.info("Searching employees with keyword: {}", keyword);
        Specification<Employee> spec = EmployeeSpecification.filter(keyword, null, null);
        return employeeRepository.findAll(spec).stream()
                .map(EmployeeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getEmployeesByDepartment(Department department) {
        log.info("Fetching employees for department: {}", department);
        return employeeRepository.findByDepartment(department).stream()
                .map(EmployeeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long total = employeeRepository.count();
        long active = employeeRepository.countByStatus(EmployeeStatus.ACTIVE);
        long inactive = employeeRepository.countByStatus(EmployeeStatus.INACTIVE);

        stats.setTotalEmployees(total);
        stats.setActiveEmployees(active);
        stats.setInactiveEmployees(inactive);

        BigDecimal avgSalary = employeeRepository.findAverageSalary();
        stats.setAverageSalary(avgSalary != null ? avgSalary.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        BigDecimal maxSalary = employeeRepository.findMaxSalary();
        stats.setHighestSalary(maxSalary != null ? maxSalary.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        BigDecimal totalSalary = employeeRepository.findTotalSalaryByStatus(EmployeeStatus.ACTIVE);
        if (totalSalary != null) {
            // Monthly payroll = active annual total / 12
            BigDecimal monthlyPayroll = totalSalary.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            stats.setTotalMonthlyPayroll(monthlyPayroll);
        } else {
            stats.setTotalMonthlyPayroll(BigDecimal.ZERO);
        }

        // Department breakdown
        List<Object[]> rawDeptStats = employeeRepository.findDepartmentStatsRaw();
        List<DepartmentStatDto> deptList = new ArrayList<>();

        for (Object[] row : rawDeptStats) {
            Department dept = (Department) row[0];
            long count = ((Number) row[1]).longValue();
            Double avg = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            BigDecimal sum = row[3] != null ? BigDecimal.valueOf(((Number) row[3]).doubleValue()) : BigDecimal.ZERO;
            double percentage = total > 0 ? ((double) count / total) * 100.0 : 0.0;

            deptList.add(new DepartmentStatDto(
                    dept,
                    count,
                    BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP),
                    sum.setScale(2, RoundingMode.HALF_UP),
                    Math.round(percentage * 10.0) / 10.0
            ));
        }

        // Sort departments by count descending
        deptList.sort((a, b) -> Long.compare(b.getEmployeeCount(), a.getEmployeeCount()));
        stats.setDepartmentStats(deptList);

        // Recent hires
        List<Employee> recent = employeeRepository.findTop5ByOrderByJoiningDateDesc();
        stats.setRecentHires(recent.stream().map(EmployeeResponseDto::fromEntity).collect(Collectors.toList()));

        return stats;
    }

    private void mapDtoToEntity(EmployeeRequestDto dto, Employee entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setDepartment(dto.getDepartment());
        entity.setDesignation(dto.getDesignation());
        entity.setSalary(dto.getSalary());
        entity.setJoiningDate(dto.getJoiningDate());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }
}
