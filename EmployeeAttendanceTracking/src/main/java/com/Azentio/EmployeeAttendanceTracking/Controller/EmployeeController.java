package com.Azentio.EmployeeAttendanceTracking.Controller;

import com.Azentio.EmployeeAttendanceTracking.Model.EmployeeModel;
import com.Azentio.EmployeeAttendanceTracking.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/addEmployee")
    public EmployeeModel addEmployee(@RequestBody EmployeeModel employeeModel) {
        return employeeService.createEmployee(employeeModel);
    }

    @PostMapping("/addAllEmployee")
    public List<EmployeeModel> addAllEmployee(@RequestBody List<EmployeeModel> employeeModels)
    {
        return employeeService.addAllEmployee(employeeModels);
    }

    @GetMapping("/getAllEmployees")
    public List<EmployeeModel> getAllEmployees() {
        return employeeService.readAllEmployees();
    }

    @GetMapping("/getEmployeeById/{employeeId}")
    public EmployeeModel getEmployeeById(@PathVariable Long employeeId) {
        return employeeService.readEmployeeById(employeeId);
    }

    @PutMapping("/updateEmployee/{employeeId}")
    public String updateEmployee(@PathVariable Long employeeId, @RequestBody EmployeeModel employeeModel) {
        return employeeService.updateEmployee(employeeId, employeeModel.getName(), employeeModel.getDepartment(), employeeModel.getPosition(), employeeModel.getContact());
    }

    @DeleteMapping("/deleteEmployee/{employeeId}")
    public String deleteEmployee(@PathVariable Long employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    @GetMapping("/getEmployeesByDepartmentId/{departmentId}")
    public List<EmployeeModel> getEmployeesByDepartmentId(Long departmentId) {
        return employeeService.getEmployeesByDepartmentId(departmentId);
    }

    @RequestMapping("/payroll/{departmentId}")
    public ResponseEntity<Object> calculatePayrollForDepartment(@PathVariable Long departmentId) {
       return  employeeService.calculatePayrollForDepartment(departmentId);
    }
}
