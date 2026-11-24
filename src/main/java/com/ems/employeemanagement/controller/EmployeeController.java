package com.ems.employeemanagement.controller;

import com.ems.employeemanagement.dto.DashboardStatsDto;
import com.ems.employeemanagement.dto.EmployeeRequestDto;
import com.ems.employeemanagement.dto.EmployeeResponseDto;
import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.EmployeeStatus;
import com.ems.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsDto stats = employeeService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("pageTitle", "HR Analytics & Overview");
        model.addAttribute("activeNav", "dashboard");
        return "dashboard";
    }

    @GetMapping("/employees")
    public String listEmployees(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "department", required = false) Department department,
            @RequestParam(name = "status", required = false) EmployeeStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size,
            @RequestParam(name = "sortBy", defaultValue = "joiningDate") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeResponseDto> employeePage = employeeService.searchAndFilterEmployees(search, department, status, pageable);

        model.addAttribute("employeePage", employeePage);
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("departments", Department.values());
        model.addAttribute("statuses", EmployeeStatus.values());
        model.addAttribute("search", search);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("pageTitle", "Employee Directory");
        model.addAttribute("activeNav", "employees");

        return "employees/index";
    }

    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("employee")) {
            EmployeeRequestDto dto = new EmployeeRequestDto();
            dto.setStatus(EmployeeStatus.ACTIVE);
            model.addAttribute("employee", dto);
        }
        model.addAttribute("departments", Department.values());
        model.addAttribute("statuses", EmployeeStatus.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Add New Employee");
        model.addAttribute("activeNav", "employees");
        return "employees/form";
    }

    @PostMapping("/employees")
    public String createEmployee(
            @Valid @ModelAttribute("employee") EmployeeRequestDto request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", Department.values());
            model.addAttribute("statuses", EmployeeStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Add New Employee");
            model.addAttribute("activeNav", "employees");
            return "employees/form";
        }

        try {
            EmployeeResponseDto created = employeeService.createEmployee(request);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Employee " + created.getFullName() + " was successfully created!");
            return "redirect:/employees";
        } catch (Exception ex) {
            log.error("Error creating employee", ex);
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("departments", Department.values());
            model.addAttribute("statuses", EmployeeStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Add New Employee");
            model.addAttribute("activeNav", "employees");
            return "employees/form";
        }
    }

    @GetMapping("/employees/{id}")
    public String viewEmployee(@PathVariable("id") Long id, Model model) {
        EmployeeResponseDto employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("pageTitle", "Employee Details: " + employee.getFullName());
        model.addAttribute("activeNav", "employees");
        return "employees/view";
    }

    @GetMapping("/employees/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("employee")) {
            EmployeeResponseDto res = employeeService.getEmployeeById(id);
            EmployeeRequestDto dto = new EmployeeRequestDto(
                    res.getId(),
                    res.getFirstName(),
                    res.getLastName(),
                    res.getEmail(),
                    res.getPhoneNumber(),
                    res.getDepartment(),
                    res.getDesignation(),
                    res.getSalary(),
                    res.getJoiningDate(),
                    res.getStatus()
            );
            model.addAttribute("employee", dto);
        }
        model.addAttribute("departments", Department.values());
        model.addAttribute("statuses", EmployeeStatus.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("pageTitle", "Edit Employee");
        model.addAttribute("activeNav", "employees");
        return "employees/form";
    }

    @PostMapping("/employees/{id}")
    public String updateEmployee(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("employee") EmployeeRequestDto request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        request.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", Department.values());
            model.addAttribute("statuses", EmployeeStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Edit Employee");
            model.addAttribute("activeNav", "employees");
            return "employees/form";
        }

        try {
            EmployeeResponseDto updated = employeeService.updateEmployee(id, request);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Employee " + updated.getFullName() + " was successfully updated!");
            return "redirect:/employees";
        } catch (Exception ex) {
            log.error("Error updating employee", ex);
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("departments", Department.values());
            model.addAttribute("statuses", EmployeeStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Edit Employee");
            model.addAttribute("activeNav", "employees");
            return "employees/form";
        }
    }

    @GetMapping("/employees/{id}/delete")
    public String deleteEmployeeGet(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        log.warn("GET request received for delete endpoint on employee id: {}. Redirecting to directory.", id);
        redirectAttributes.addFlashAttribute("errorMessage", 
                "Deletion must be confirmed through the employee management interface.");
        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            EmployeeResponseDto emp = null;
            try {
                emp = employeeService.getEmployeeById(id);
            } catch (Exception ignored) {
                // Ignore if employee cannot be retrieved prior to delete
            }
            employeeService.deleteEmployee(id);
            String name = (emp != null && emp.getFullName() != null && !emp.getFullName().isBlank()) 
                    ? emp.getFullName() 
                    : ("ID #" + id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Employee " + name + " has been permanently deleted.");
        } catch (Exception ex) {
            log.error("Error deleting employee with id: {}", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to delete employee: " + ex.getMessage());
        }
        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/toggle-status")
    public String toggleStatus(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Referer", required = false) String referer,
            RedirectAttributes redirectAttributes) {

        EmployeeResponseDto updated = employeeService.toggleEmployeeStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Status for " + updated.getFullName() + " is now " + updated.getStatusDisplayName() + ".");

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/employees";
    }
}
