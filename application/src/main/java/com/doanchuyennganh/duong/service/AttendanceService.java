package com.doanchuyennganh.duong.service;
import com.doanchuyennganh.duong.model.Attendance;
import com.doanchuyennganh.duong.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class AttendanceService {
    private final AttendanceRepository repo;

    public AttendanceService(AttendanceRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Attendance checkIn(Integer userId, LocalDate date) {
        if (repo.existsByUserIdAndCheckinDate(userId, date)) {
            return null; // đã checkin
        }
        Attendance a = new Attendance(userId, date);
        return repo.save(a);
    }

    public boolean hasCheckedInToday(Integer userId, LocalDate date) {
        return repo.existsByUserIdAndCheckinDate(userId, date);
    }

    public List<LocalDate> getCheckinDates(Integer userId) {
        return repo.findDatesByUserId(userId);
    }

    public long countThisWeek(Integer userId, LocalDate referenceDate) {
        WeekFields wf = WeekFields.of(Locale.getDefault());
        LocalDate start = referenceDate.with(wf.dayOfWeek(), 1);
        LocalDate end = referenceDate.with(wf.dayOfWeek(), 7);
        return repo.countBetweenDates(userId, start, end);
    }

    public long countThisMonth(Integer userId, LocalDate referenceDate) {
        LocalDate start = referenceDate.withDayOfMonth(1);
        LocalDate end = referenceDate.withDayOfMonth(referenceDate.lengthOfMonth());
        return repo.countBetweenDates(userId, start, end);
    }
}
