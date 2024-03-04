package com.Azentio.EmployeeAttendanceTracking.Service;


import com.Azentio.EmployeeAttendanceTracking.Model.EmployeeModel;
import com.Azentio.EmployeeAttendanceTracking.Model.LeaveRequestModel;
import com.Azentio.EmployeeAttendanceTracking.Repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveRequestService
{
    @Autowired
    LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestModel createLeaveRequest(LeaveRequestModel leaveRequestModel) {
        return leaveRequestRepository.save(leaveRequestModel);
    }

    public List<LeaveRequestModel> createAllLeaveRequest(List<LeaveRequestModel> leaveRequestModel) {
        return leaveRequestRepository.saveAll(leaveRequestModel);
    }

    public List<LeaveRequestModel> readAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequestModel readLeaveRequestById(Long employeeId) {
        return leaveRequestRepository.findById(employeeId).orElse(null);
    }

    public String updateLeaveRequest(Long employeeId, LocalDate startDate, LocalDate endDate, String status) {
        LeaveRequestModel request = leaveRequestRepository.findById(employeeId).orElse(null);
        if (request != null) {
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setStatus(status);
            leaveRequestRepository.save(request);
            return "Leave Request Details Updated";
        } else {
            return "Leave Request Not Found";
        }
    }

    public String deleteLeaveRequest(Long employeeId) {
        if (leaveRequestRepository.existsById(employeeId)) {
            leaveRequestRepository.deleteById(employeeId);
            return "Leave Request Deleted";
        } else {
            return "Leave Request Not Found";
        }
    }

    public void updateTotalNumberOfDays(Long requestId) {
        LeaveRequestModel leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request Not Found"));

        int totalNumberOfDays = (int) ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
        leaveRequest.setTotalNumberOfDays(totalNumberOfDays);
        leaveRequest.setStatus("Approved");

        leaveRequestRepository.save(leaveRequest);
    }
}