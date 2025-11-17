package com.doanchuyennganh.duong.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "checkin_date"})
})
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attendanceId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    // constructors, getters, setters
    public Attendance() {}
    public Attendance(Integer userId, LocalDate checkinDate) {
        this.userId = userId;
        this.checkinDate = checkinDate;
    }
    public Integer getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Integer attendanceId) { this.attendanceId = attendanceId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public LocalDate getCheckinDate() { return checkinDate; }
    public void setCheckinDate(LocalDate checkinDate) { this.checkinDate = checkinDate; }
}

