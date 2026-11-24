package com.ems.employeemanagement.repository;

import com.ems.employeemanagement.entity.Department;
import com.ems.employeemanagement.entity.Employee;
import com.ems.employeemanagement.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByStatus(EmployeeStatus status);

    long countByDepartment(Department department);

    @Query("SELECT AVG(e.salary) FROM Employee e")
    BigDecimal findAverageSalary();

    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.status = :status")
    BigDecimal findAverageSalaryByStatus(@Param("status") EmployeeStatus status);

    @Query("SELECT SUM(e.salary) FROM Employee e WHERE e.status = :status")
    BigDecimal findTotalSalaryByStatus(@Param("status") EmployeeStatus status);

    @Query("SELECT MAX(e.salary) FROM Employee e")
    BigDecimal findMaxSalary();

    @Query("SELECT e.department, COUNT(e), AVG(e.salary), SUM(e.salary) FROM Employee e GROUP BY e.department")
    List<Object[]> findDepartmentStatsRaw();

    List<Employee> findTop5ByOrderByJoiningDateDesc();

    List<Employee> findByDepartment(Department department);

    List<Employee> findByStatus(EmployeeStatus status);
}
