package com.ems.employeemanagement.controller;

import com.ems.employeemanagement.dto.DashboardStatsDto;
import com.ems.employeemanagement.dto.EmployeeResponseDto;
import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.Employee;
import com.ems.employeemanagement.entity.EmployeeStatus;
import com.ems.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeResponseDto sampleResponse;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee(1L, "Alice", "Smith", "alice.smith@company.com", "+1 555-0155",
                Department.ENGINEERING, "Senior Software Engineer", new BigDecimal("130000.00"),
                LocalDate.of(2022, 5, 20), EmployeeStatus.ACTIVE);
        sampleResponse = EmployeeResponseDto.fromEntity(employee);
    }

    @Test
    @DisplayName("GET / - Should redirect to /dashboard")
    void root_RedirectsToDashboard() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @DisplayName("GET /dashboard - Should parse and render dashboard.html")
    void dashboard_RendersSuccessfully() throws Exception {
        when(employeeService.getDashboardStats()).thenReturn(new DashboardStatsDto());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("stats", "pageTitle", "activeNav"));
    }

    @Test
    @DisplayName("GET /employees - Should parse and render employees/index.html")
    void employeesList_RendersSuccessfully() throws Exception {
        when(employeeService.searchAndFilterEmployees(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/index"))
                .andExpect(model().attributeExists("employees", "employeePage", "departments", "statuses"));
    }

    @Test
    @DisplayName("GET /employees/new - Should parse and render employees/form.html for create")
    void showCreateForm_RendersSuccessfully() throws Exception {
        mockMvc.perform(get("/employees/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employee", "departments", "statuses", "isEdit"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("GET /employees/{id} - Should parse and render employees/view.html")
    void viewEmployee_RendersSuccessfully() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/view"))
                .andExpect(model().attributeExists("employee", "pageTitle"));
    }

    @Test
    @DisplayName("GET /employees/{id}/edit - Should parse and render employees/form.html for edit")
    void showEditForm_RendersSuccessfully() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/employees/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employee", "isEdit"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @DisplayName("POST /employees - Should redirect to /employees on valid create")
    void createEmployee_RedirectsOnSuccess() throws Exception {
        when(employeeService.createEmployee(any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/employees")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@company.com")
                        .param("phoneNumber", "+1 555-0100")
                        .param("department", "ENGINEERING")
                        .param("designation", "Software Engineer")
                        .param("salary", "100000.00")
                        .param("joiningDate", "2023-01-01")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
