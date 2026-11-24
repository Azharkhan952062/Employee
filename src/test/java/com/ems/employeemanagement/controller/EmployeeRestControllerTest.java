package com.ems.employeemanagement.controller;

import com.ems.employeemanagement.dto.EmployeeRequestDto;
import com.ems.employeemanagement.dto.EmployeeResponseDto;
import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.Employee;
import com.ems.employeemanagement.entity.EmployeeStatus;
import com.ems.employeemanagement.exception.DuplicateResourceException;
import com.ems.employeemanagement.exception.ResourceNotFoundException;
import com.ems.employeemanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeRestController.class)
class EmployeeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeResponseDto sampleResponse;
    private EmployeeRequestDto sampleValidRequest;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee(1L, "Alice", "Smith", "alice.smith@company.com", "+1 555-0155",
                Department.ENGINEERING, "Senior Software Engineer", new BigDecimal("130000.00"),
                LocalDate.of(2022, 5, 20), EmployeeStatus.ACTIVE);
        sampleResponse = EmployeeResponseDto.fromEntity(employee);

        sampleValidRequest = new EmployeeRequestDto(null, "Alice", "Smith", "alice.smith@company.com", "+1 555-0155",
                Department.ENGINEERING, "Senior Software Engineer", new BigDecimal("130000.00"),
                LocalDate.of(2022, 5, 20), EmployeeStatus.ACTIVE);
    }

    @Test
    @DisplayName("GET /api/employees - Should return paginated list of employees")
    void getEmployees_ReturnsOk() throws Exception {
        when(employeeService.searchAndFilterEmployees(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].email").value("alice.smith@company.com"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} - Should return employee when exists")
    void getEmployeeById_Success() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Alice Smith"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} - Should return 404 when employee not found")
    void getEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(999L))
                .thenThrow(new ResourceNotFoundException("Employee", "id", 999L));

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Employee not found with id: '999'")));
    }

    @Test
    @DisplayName("POST /api/employees - Should create employee when payload is valid")
    void createEmployee_Valid_Returns201() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleValidRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value("alice.smith@company.com"));
    }

    @Test
    @DisplayName("POST /api/employees - Should return 400 when validation fails on required and invalid fields")
    void createEmployee_InvalidData_Returns400() throws Exception {
        EmployeeRequestDto invalidRequest = new EmployeeRequestDto();
        invalidRequest.setFirstName(""); // Blank
        invalidRequest.setLastName("");  // Blank
        invalidRequest.setEmail("invalid-email-format"); // Bad email
        invalidRequest.setSalary(new BigDecimal("-100.00")); // Negative salary
        invalidRequest.setJoiningDate(LocalDate.now().plusDays(10)); // Future date

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.firstName").exists())
                .andExpect(jsonPath("$.data.lastName").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.salary").exists())
                .andExpect(jsonPath("$.data.joiningDate").exists());

        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    @DisplayName("POST /api/employees - Should return 409 when duplicate email is supplied")
    void createEmployee_DuplicateEmail_Returns409() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDto.class)))
                .thenThrow(new DuplicateResourceException("Employee", "email", "alice.smith@company.com"));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleValidRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    @DisplayName("PUT /api/employees/{id} - Should update employee successfully")
    void updateEmployee_Success() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDto.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleValidRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee updated successfully"));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} - Should delete employee successfully")
    void deleteEmployee_Success() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));

        verify(employeeService, times(1)).deleteEmployee(1L);
    }

    @Test
    @DisplayName("GET /api/employees/search - Should return matching employees")
    void searchEmployees_Success() throws Exception {
        when(employeeService.searchEmployees("Alice")).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/employees/search").param("keyword", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"));
    }

    @Test
    @DisplayName("GET /api/employees/department/{department} - Should return employees in department")
    void getEmployeesByDepartment_Success() throws Exception {
        when(employeeService.getEmployeesByDepartment(Department.ENGINEERING)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/employees/department/ENGINEERING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].department").value("ENGINEERING"));
    }
}
