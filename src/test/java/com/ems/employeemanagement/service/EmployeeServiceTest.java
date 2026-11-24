package com.ems.employeemanagement.service;

import com.ems.employeemanagement.dto.DashboardStatsDto;
import com.ems.employeemanagement.dto.EmployeeRequestDto;
import com.ems.employeemanagement.dto.EmployeeResponseDto;
import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.Employee;
import com.ems.employeemanagement.entity.EmployeeStatus;
import com.ems.employeemanagement.exception.DuplicateResourceException;
import com.ems.employeemanagement.exception.ResourceNotFoundException;
import com.ems.employeemanagement.repository.EmployeeRepository;
import com.ems.employeemanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee sampleEmployee;
    private EmployeeRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleEmployee = new Employee(1L, "John", "Doe", "john.doe@company.com", "+1 555-0100",
                Department.ENGINEERING, "Senior Developer", new BigDecimal("120000.00"),
                LocalDate.of(2023, 1, 15), EmployeeStatus.ACTIVE);

        sampleRequest = new EmployeeRequestDto(null, "John", "Doe", "john.doe@company.com", "+1 555-0100",
                Department.ENGINEERING, "Senior Developer", new BigDecimal("120000.00"),
                LocalDate.of(2023, 1, 15), EmployeeStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should successfully create an employee when email is unique")
    void createEmployee_Success() {
        when(employeeRepository.existsByEmail("john.doe@company.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponseDto result = employeeService.createEmployee(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@company.com");
        assertThat(result.getFullName()).isEqualTo("John Doe");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void createEmployee_DuplicateEmail_ThrowsException() {
        when(employeeRepository.existsByEmail("john.doe@company.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(sampleRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should return employee by id when found")
    void getEmployeeById_Found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponseDto result = employeeService.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDepartment()).isEqualTo(Department.ENGINEERING);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee id does not exist")
    void getEmployeeById_NotFound_ThrowsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id: '99'");
    }

    @Test
    @DisplayName("Should toggle employee status from Active to Inactive")
    void toggleEmployeeStatus_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponseDto result = employeeService.toggleEmployeeStatus(1L);

        assertThat(result.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        verify(employeeRepository, times(1)).save(sampleEmployee);
    }

    @Test
    @DisplayName("Should successfully update employee when valid")
    void updateEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.existsByEmailAndIdNot("john.doe@company.com", 1L)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        sampleRequest.setDesignation("Lead Architect");
        EmployeeResponseDto result = employeeService.updateEmployee(1L, sampleRequest);

        assertThat(result).isNotNull();
        verify(employeeRepository, times(1)).save(sampleEmployee);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent employee")
    void updateEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(99L, sampleRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating with existing email of another employee")
    void updateEmployee_DuplicateEmail_ThrowsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.existsByEmailAndIdNot("existing@company.com", 1L)).thenReturn(true);

        sampleRequest.setEmail("existing@company.com");
        assertThatThrownBy(() -> employeeService.updateEmployee(1L, sampleRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should successfully delete employee when found")
    void deleteEmployee_Success() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent employee")
    void deleteEmployee_NotFound_ThrowsException() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should return all employees")
    void getAllEmployees_Success() {
        when(employeeRepository.findAll()).thenReturn(List.of(sampleEmployee));

        List<EmployeeResponseDto> list = employeeService.getAllEmployees();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getEmail()).isEqualTo("john.doe@company.com");
    }

    @Test
    @DisplayName("Should return employees by department")
    void getEmployeesByDepartment_Success() {
        when(employeeRepository.findByDepartment(Department.ENGINEERING)).thenReturn(List.of(sampleEmployee));

        List<EmployeeResponseDto> list = employeeService.getEmployeesByDepartment(Department.ENGINEERING);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDepartment()).isEqualTo(Department.ENGINEERING);
    }

    @Test
    @DisplayName("Should calculate dashboard stats correctly from repository aggregates")
    void getDashboardStats_Success() {
        when(employeeRepository.count()).thenReturn(10L);
        when(employeeRepository.countByStatus(EmployeeStatus.ACTIVE)).thenReturn(8L);
        when(employeeRepository.countByStatus(EmployeeStatus.INACTIVE)).thenReturn(2L);
        when(employeeRepository.findAverageSalary()).thenReturn(new BigDecimal("110000.00"));
        when(employeeRepository.findMaxSalary()).thenReturn(new BigDecimal("150000.00"));
        when(employeeRepository.findTotalSalaryByStatus(EmployeeStatus.ACTIVE)).thenReturn(new BigDecimal("880000.00"));
        when(employeeRepository.findDepartmentStatsRaw()).thenReturn(Collections.emptyList());
        when(employeeRepository.findTop5ByOrderByJoiningDateDesc()).thenReturn(List.of(sampleEmployee));

        DashboardStatsDto stats = employeeService.getDashboardStats();

        assertThat(stats.getTotalEmployees()).isEqualTo(10L);
        assertThat(stats.getActiveEmployees()).isEqualTo(8L);
        assertThat(stats.getInactiveEmployees()).isEqualTo(2L);
        assertThat(stats.getAverageSalary()).isEqualByComparingTo(new BigDecimal("110000.00"));
        assertThat(stats.getRecentHires()).hasSize(1);
    }
}
