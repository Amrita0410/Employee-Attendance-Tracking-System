package com.Azentio.EmployeeAttendanceTracking.Controller;

import com.Azentio.EmployeeAttendanceTracking.Model.EmployeeModel;
import com.Azentio.EmployeeAttendanceTracking.Model.LeaveRequestModel;
import com.Azentio.EmployeeAttendanceTracking.Service.EmployeeService;
import com.Azentio.EmployeeAttendanceTracking.Service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/LeaveRequest")
public class LeaveRequestController
{
    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/addLeaveRequest")
    public LeaveRequestModel addLeaveRequest(@RequestBody LeaveRequestModel leaveRequestModel) {
        return leaveRequestService.createLeaveRequest(leaveRequestModel);
    }

    @PostMapping("/addAllLeaveRequest")
    public List<LeaveRequestModel> addAllLeaveRequest(@RequestBody List<LeaveRequestModel> leaveRequestModels) {
        return leaveRequestService.createAllLeaveRequest(leaveRequestModels);
    }

    @GetMapping("/getAllLeaveRequests")
    public List<LeaveRequestModel> getAllLeaveRequests() {
        return leaveRequestService.readAllLeaveRequests();
    }

    @GetMapping("/getLeaveRequestById/{employeeId}")
    public LeaveRequestModel getLeaveRequestById(@PathVariable Long employeeId) {
        return leaveRequestService.readLeaveRequestById(employeeId);
    }

    @PutMapping("/updateLeaveRequest/{employeeId}")
    public String updateLeaveRequest(@PathVariable Long employeeId, @RequestBody LeaveRequestModel leaveRequestModel) {
        return leaveRequestService.updateLeaveRequest(employeeId, leaveRequestModel.getStartDate(), leaveRequestModel.getEndDate(), leaveRequestModel.getStatus());
    }

    @DeleteMapping("/deleteLeaveRequest/{employeeId}")
    public String deleteLeaveRequest(@PathVariable Long employeeId) {
        return leaveRequestService.deleteLeaveRequest(employeeId);
    }

    @PostMapping("/{requestId}/update-total-days")
    public String updateTotalNumberOfDays(@PathVariable Long requestId) {
            leaveRequestService.updateTotalNumberOfDays(requestId);
            return "Total number of days updated successfully.";
    }
}
