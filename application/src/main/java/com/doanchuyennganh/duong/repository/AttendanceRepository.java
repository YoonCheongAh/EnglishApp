package com.doanchuyennganh.duong.repository;

import com.doanchuyennganh.duong.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    boolean existsByUserIdAndCheckinDate(Integer userId, LocalDate date);

    @Query("SELECT a.checkinDate FROM Attendance a WHERE a.userId = :userId")
    List<LocalDate> findDatesByUserId(Integer userId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.userId = :userId AND a.checkinDate BETWEEN :start AND :end")
    long countBetweenDates(Integer userId, LocalDate start, LocalDate end);
}

