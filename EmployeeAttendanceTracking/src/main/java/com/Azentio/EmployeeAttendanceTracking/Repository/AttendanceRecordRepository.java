package com.Azentio.EmployeeAttendanceTracking.Repository;

import com.Azentio.EmployeeAttendanceTracking.Model.AttendanceRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecordModel, Integer>
{
    @Modifying
    @Transactional
    @Query("UPDATE AttendanceRecordModel a SET a.date = :date, a.startTime = :startTime, a.endTime = :endTime WHERE a.employeeId = :employeeId")
    void updateAttendanceRecord(@Param("employeeId") Long employeeId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM AttendanceRecordModel a WHERE a.employeeId = :employeeId")
    void deleteAttendanceRecord(@Param("employeeId") Long employeeId);

    List<AttendanceRecordModel> findByEmployeeId(Long employeeId);

    @Query("SELECT e, a FROM EmployeeModel e LEFT JOIN AttendanceRecordModel a ON e.employeeId = a.employeeId WHERE e.departmentId = :departmentId")
    List<Object[]> findEmployeesAndAttendanceRecordsByDepartmentId(@Param("departmentId") Long departmentId);


}
