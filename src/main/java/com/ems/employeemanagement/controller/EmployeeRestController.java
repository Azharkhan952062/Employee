package com.ems.employeemanagement.controller;

import com.ems.employeemanagement.dto.ApiResponse;
import com.ems.employeemanagement.dto.DashboardStatsDto;
import com.ems.employeemanagement.dto.EmployeeRequestDto;
import com.ems.employeemanagement.dto.EmployeeResponseDto;
import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.EmployeeStatus;
import com.ems.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponseDto>>> getEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmployeeResponseDto> result = employeeService.searchAndFilterEmployees(search, department, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Employees fetched successfully", result));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> getAllEmployees() {
        List<EmployeeResponseDto> list = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.ok("All employees retrieved", list));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> searchEmployees(@RequestParam(name = "keyword", required = false) String keyword) {
        List<EmployeeResponseDto> list = employeeService.searchEmployees(keyword != null ? keyword : "");
        return ResponseEntity.ok(ApiResponse.ok("Search results retrieved", list));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> getEmployeesByDepartment(@PathVariable("department") String department) {
        Department dept = Department.fromString(department);
        List<EmployeeResponseDto> list = employeeService.getEmployeesByDepartment(dept);
        return ResponseEntity.ok(ApiResponse.ok("Employees for department " + dept.getDisplayName() + " retrieved", list));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = employeeService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok("Dashboard statistics retrieved", stats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee found", employee));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> createEmployee(@Valid @RequestBody EmployeeRequestDto request) {
        EmployeeResponseDto created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Employee created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto request) {
        EmployeeResponseDto updated = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Employee updated successfully", updated));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> toggleStatus(@PathVariable Long id) {
        EmployeeResponseDto updated = employeeService.toggleEmployeeStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted successfully", null));
    }
}
