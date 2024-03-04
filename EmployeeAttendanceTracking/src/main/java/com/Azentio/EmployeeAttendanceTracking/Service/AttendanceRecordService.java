package com.Azentio.EmployeeAttendanceTracking.Service;

import com.Azentio.EmployeeAttendanceTracking.Model.AttendanceRecordModel;
import com.Azentio.EmployeeAttendanceTracking.Model.EmployeeModel;
import com.Azentio.EmployeeAttendanceTracking.Repository.AttendanceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceRecordService
{
    @Autowired
    AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    EmployeeService employeeService;

    public AttendanceRecordModel createAttendanceRecord(AttendanceRecordModel attendanceRecordModel) {
        return attendanceRecordRepository.save(attendanceRecordModel);
    }

    public List<AttendanceRecordModel> createAllAttendanceRecord(List<AttendanceRecordModel> attendanceRecordModel) {
        return attendanceRecordRepository.saveAll(attendanceRecordModel);
    }

    public List<AttendanceRecordModel> readAllAttendanceRecords() {
        return attendanceRecordRepository.findAll();
    }

    public AttendanceRecordModel readAttendanceRecordById(Long employeeId) {
        return attendanceRecordRepository.findById(Math.toIntExact(employeeId)).orElse(null);
    }

    public String updateAttendanceRecord(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        AttendanceRecordModel record = attendanceRecordRepository.findById(Math.toIntExact(employeeId)).orElse(null);
        if ( record != null) {
            record.setDate(date);
            record.setStartTime(startTime);
            record.setEndTime(endTime);
            attendanceRecordRepository.save(record);
            return "Attendance Record Details Updated";
        } else {
            return "Attendance Record not found";
        }
    }

    public String deleteAttendanceRecord(Long employeeId) {
        if (attendanceRecordRepository.existsById(Math.toIntExact(employeeId)))
        {
            attendanceRecordRepository.deleteById(Math.toIntExact(employeeId));
            return "Attendance Record Deleted";
        } else
        {
            return "Attendance Record not found";
        }
    }

    public List<AttendanceSummary> calculateWorkingHoursPerDay(Long employeeId) {
        List<AttendanceRecordModel> attendanceRecords = attendanceRecordRepository.findByEmployeeId(employeeId);
//        List<AttendanceRecordModel> filteredAttendanceRecords = attendanceRecords.stream()
//                .filter(record -> record.getEmployeeId() == employeeId)
//                .collect(Collectors.toList());

        List<AttendanceSummary> attendanceSummaries = new ArrayList<>();
        for (AttendanceRecordModel record : attendanceRecords) {
            Duration workingHours = calculateWorkingHours(record.getStartTime(), record.getEndTime());
            AttendanceSummary attendanceSummary = new AttendanceSummary(record.getEmployeeId(), record.getDate(), workingHours);
            attendanceSummaries.add(attendanceSummary);
        }

        return attendanceSummaries;
    }

    public List<AttendanceSummary> calculateWorkingHoursPerDayForDepartment(Long departmentId) {
        List<Object[]> employeeAndAttendanceRecords = attendanceRecordRepository.findEmployeesAndAttendanceRecordsByDepartmentId(departmentId);
        List<AttendanceSummary> attendanceSummaries = new ArrayList<>();

        for (Object[] result : employeeAndAttendanceRecords) {
            EmployeeModel employee = (EmployeeModel) result[0];
            AttendanceRecordModel record = (AttendanceRecordModel) result[1];

            Duration workingHours = calculateWorkingHours(record.getStartTime(), record.getEndTime());
            AttendanceSummary attendanceSummary = new AttendanceSummary(employee.getEmployeeId(), record.getDate(), workingHours);
            attendanceSummaries.add(attendanceSummary);
        }

        return attendanceSummaries;
    }

    private Duration calculateWorkingHours(LocalTime startTime, LocalTime endTime) {
        return Duration.between(startTime, endTime);
    }




    public static class AttendanceSummary {
        private long employeeId;
        private LocalDate date;
        private Duration workingHours;

        public AttendanceSummary(long employeeId, LocalDate date, Duration workingHours) {
            this.employeeId = employeeId;
            this.date = date;
            this.workingHours = workingHours;
        }

        public long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(long employeeId) {
            this.employeeId = employeeId;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public Duration getWorkingHours() {
            return workingHours;
        }

        public void setWorkingHours(Duration workingHours) {
            this.workingHours = workingHours;
        }

        public String getFormattedWorkingHours() {
            long hours = workingHours.toHours();
            long minutes = workingHours.minusHours(hours).toMinutes();
            return hours + " hours " + minutes + " minutes";
        }
    }

    public void calculateAndSetOvertimeForEmployee(Long employeeId, Long regularHoursThreshold) {
        List<AttendanceRecordModel> attendanceRecords = attendanceRecordRepository.findByEmployeeId(employeeId);

        for (AttendanceRecordModel record : attendanceRecords) {
            Duration totalWorkingHours = calculateWorkingHours(record.getStartTime(), record.getEndTime());
            Duration regularHours = Duration.ofHours(regularHoursThreshold);
            Duration overtime = totalWorkingHours.minus(regularHours);

            if (overtime.toMinutes() > 0) {
                record.setOvertime(overtime);
                attendanceRecordRepository.save(record);
                System.out.println("Overtime calculated and updated for employee with ID: " + employeeId);
            }
            else {
                System.out.println("Employee with ID "+ employeeId +" has not worked overtime.");
            }
        }
    }



}
