package com.Azentio.EmployeeAttendanceTracking.Service;

import com.Azentio.EmployeeAttendanceTracking.Model.AttendanceRecordModel;
import com.Azentio.EmployeeAttendanceTracking.Model.EmployeeModel;
import com.Azentio.EmployeeAttendanceTracking.Repository.AttendanceRecordRepository;
import com.Azentio.EmployeeAttendanceTracking.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AttendanceRecordRepository attendanceRecordRepository;

    public EmployeeModel createEmployee(EmployeeModel employeeModel) {
        return employeeRepository.save(employeeModel);
    }

    public List<EmployeeModel> addAllEmployee(List<EmployeeModel> employees) {
        return employeeRepository.saveAll(employees);
    }

    public List<EmployeeModel> readAllEmployees() {
        return employeeRepository.findAll();
    }

    public EmployeeModel readEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId).orElse(null);
    }

    public String updateEmployee(Long employeeId, String name, String department, String position, String contact) {
        EmployeeModel employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            employee.setName(name);
            employee.setDepartment(department);
            employee.setPosition(position);
            employee.setContact(contact);
            employeeRepository.save(employee);
            return "Employee Details Updated";
        } else {
            return "Employee not found";
        }
    }

    public String deleteEmployee(Long employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
            return "Employee Deleted";
        } else {
            return "Employee not found";
        }
    }

    public List<EmployeeModel> getEmployeesByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartment_Id(departmentId);
    }

    @Transactional
    public void updateHourlyRates() {
        employeeRepository.updateHourlyRateByPosition("Manager", 10000);
        employeeRepository.updateHourlyRateByPosition("Procurement Officer", 8000);
        employeeRepository.updateHourlyRateByPosition("Accountant", 7000);
        employeeRepository.updateHourlyRateByPosition("Co-ordinator", 6000);
        employeeRepository.updateHourlyRateByPosition("Marketing Specialist", 5000);
        employeeRepository.updateHourlyRateByPosition("Sales Representative", 4000);
    }

    public ResponseEntity<Object> calculatePayrollForDepartment(Long departmentId) {
        setHourlyRates();

        List<EmployeeModel> employeesInDepartment = employeeRepository.findByDepartmentId(departmentId);
        List<Map<String, Object>> payrollResults = new ArrayList<>();

        for (EmployeeModel employee : employeesInDepartment) {
            double hourlyRate = employee.getHourlyRate();
            List<AttendanceRecordModel> attendanceRecords = attendanceRecordRepository.findByEmployeeId(employee.getEmployeeId());

            double totalPay = 0;
            double totalRegularPay = 0;
            double totalOvertimePay = 0;
            Map<String, Object> payrollDetails = new HashMap<>();

            for (AttendanceRecordModel record : attendanceRecords) {
                Duration workingHours = Duration.between(record.getStartTime(), record.getEndTime());
                double hoursWorked = workingHours.toMinutes() / 60.0;

                double regularPay = hoursWorked * hourlyRate;
                totalRegularPay += regularPay;

                if (hoursWorked > 8) {
                    double overtimeHours = hoursWorked - 8;
                    double overtimePay = overtimeHours * hourlyRate;
                    totalOvertimePay += overtimePay;
                }
            }
            totalPay = totalRegularPay + totalOvertimePay;

            payrollDetails.put("employeeId", employee.getEmployeeId());
            payrollDetails.put("regularPay", totalRegularPay);
            payrollDetails.put("overtimePay", totalOvertimePay);
            payrollDetails.put("totalPay", totalPay);
            payrollResults.add(payrollDetails);

            employee.setPayroll(totalPay);
            employeeRepository.save(employee);
        }

        return ResponseEntity.ok(payrollResults);
    }

    private void setHourlyRates() {
        Map<String, Double> positionHourlyRates = new HashMap<>();
        positionHourlyRates.put("Manager", 10000.0);
        positionHourlyRates.put("Procurement Officer", 8000.0);
        positionHourlyRates.put("Accountant", 9000.0);
        positionHourlyRates.put("Co-ordinator", 7000.0);
        positionHourlyRates.put("Marketing Specialist", 7000.0);
        positionHourlyRates.put("Sales Representative", 4000.0);

        List<EmployeeModel> employees = employeeRepository.findAll();
        for (EmployeeModel employee : employees) {
            String position = employee.getPosition();
            if (positionHourlyRates.containsKey(position)) {
                employee.setHourlyRate(positionHourlyRates.get(position));
                employeeRepository.save(employee);
            }
        }
    }
}
