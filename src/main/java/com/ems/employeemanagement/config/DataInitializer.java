package com.ems.employeemanagement.config;

import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.Employee;
import com.ems.employeemanagement.entity.EmployeeStatus;
import com.ems.employeemanagement.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final EmployeeRepository employeeRepository;

    public DataInitializer(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() > 0) {
            log.info("Database already contains employees. Skipping initial seeding.");
            return;
        }

        log.info("Database is empty. Seeding initial employee records for production HR dashboard...");

        List<Employee> initialEmployees = List.of(
                new Employee(null, "Alexander", "Wright", "alexander.wright@company.com", "+1 415-555-0101",
                        Department.ENGINEERING, "Lead Software Architect", new BigDecimal("148000.00"),
                        LocalDate.of(2021, 3, 15), EmployeeStatus.ACTIVE),

                new Employee(null, "Sophia", "Chen", "sophia.chen@company.com", "+1 415-555-0102",
                        Department.ENGINEERING, "Senior Full Stack Engineer", new BigDecimal("125000.00"),
                        LocalDate.of(2022, 1, 10), EmployeeStatus.ACTIVE),

                new Employee(null, "Marcus", "Johnson", "marcus.johnson@company.com", "+1 415-555-0103",
                        Department.ENGINEERING, "DevOps & Cloud Engineer", new BigDecimal("118000.00"),
                        LocalDate.of(2023, 6, 1), EmployeeStatus.ACTIVE),

                new Employee(null, "Elena", "Rostova", "elena.rostova@company.com", "+1 415-555-0104",
                        Department.HUMAN_RESOURCES, "VP of People Operations", new BigDecimal("132000.00"),
                        LocalDate.of(2020, 8, 20), EmployeeStatus.ACTIVE),

                new Employee(null, "David", "Kim", "david.kim@company.com", "+1 415-555-0105",
                        Department.HUMAN_RESOURCES, "Talent Acquisition Specialist", new BigDecimal("82000.00"),
                        LocalDate.of(2022, 11, 14), EmployeeStatus.ACTIVE),

                new Employee(null, "Olivia", "Martinez", "olivia.martinez@company.com", "+1 415-555-0106",
                        Department.MARKETING, "Chief Marketing Officer", new BigDecimal("140000.00"),
                        LocalDate.of(2021, 5, 4), EmployeeStatus.ACTIVE),

                new Employee(null, "Liam", "Taylor", "liam.taylor@company.com", "+1 415-555-0107",
                        Department.MARKETING, "Growth Marketing Lead", new BigDecimal("95000.00"),
                        LocalDate.of(2023, 2, 18), EmployeeStatus.ACTIVE),

                new Employee(null, "Isabella", "Santos", "isabella.santos@company.com", "+1 415-555-0108",
                        Department.FINANCE, "Financial Controller", new BigDecimal("128000.00"),
                        LocalDate.of(2020, 11, 9), EmployeeStatus.ACTIVE),

                new Employee(null, "Ethan", "Patel", "ethan.patel@company.com", "+1 415-555-0109",
                        Department.FINANCE, "Senior Financial Analyst", new BigDecimal("98000.00"),
                        LocalDate.of(2022, 7, 25), EmployeeStatus.ACTIVE),

                new Employee(null, "Charlotte", "Davies", "charlotte.davies@company.com", "+1 415-555-0110",
                        Department.SALES, "Enterprise Account Director", new BigDecimal("135000.00"),
                        LocalDate.of(2021, 9, 12), EmployeeStatus.ACTIVE),

                new Employee(null, "James", "Wilson", "james.wilson@company.com", "+1 415-555-0111",
                        Department.SALES, "Business Development Representative", new BigDecimal("75000.00"),
                        LocalDate.of(2023, 9, 5), EmployeeStatus.ACTIVE),

                new Employee(null, "Emily", "Zhang", "emily.zhang@company.com", "+1 415-555-0112",
                        Department.PRODUCT, "Head of Product", new BigDecimal("145000.00"),
                        LocalDate.of(2021, 1, 18), EmployeeStatus.ACTIVE),

                new Employee(null, "Noah", "Bennett", "noah.bennett@company.com", "+1 415-555-0113",
                        Department.PRODUCT, "Product Designer (UI/UX)", new BigDecimal("105000.00"),
                        LocalDate.of(2023, 4, 11), EmployeeStatus.ACTIVE),

                new Employee(null, "Aria", "Kowalski", "aria.kowalski@company.com", "+1 415-555-0114",
                        Department.OPERATIONS, "Director of Business Operations", new BigDecimal("122000.00"),
                        LocalDate.of(2022, 3, 30), EmployeeStatus.ACTIVE),

                new Employee(null, "Lucas", "Gray", "lucas.gray@company.com", "+1 415-555-0115",
                        Department.LEGAL, "Senior Corporate Counsel", new BigDecimal("150000.00"),
                        LocalDate.of(2020, 4, 15), EmployeeStatus.ACTIVE),

                new Employee(null, "Harper", "Scott", "harper.scott@company.com", "+1 415-555-0116",
                        Department.ENGINEERING, "Junior QA Automation Engineer", new BigDecimal("68000.00"),
                        LocalDate.of(2023, 11, 20), EmployeeStatus.INACTIVE)
        );

        employeeRepository.saveAll(initialEmployees);
        log.info("Successfully seeded {} initial employee records.", initialEmployees.size());
    }
}
