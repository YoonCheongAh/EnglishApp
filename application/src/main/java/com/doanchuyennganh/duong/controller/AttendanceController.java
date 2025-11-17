package com.doanchuyennganh.duong.controller;
import com.doanchuyennganh.duong.dto.CheckinResponse;
import com.doanchuyennganh.duong.dto.CountsResponse;
import com.doanchuyennganh.duong.dto.DatesResponse;
import com.doanchuyennganh.duong.service.AttendanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    // POST /api/attendance/checkin  { "userId": 1 }
    @PostMapping("/checkin")
    public ResponseEntity<CheckinResponse> checkIn(@RequestBody java.util.Map<String, Object> body) {
        Integer userId = (Integer) body.get("userId");
        if (userId == null) {
            CheckinResponse r = new CheckinResponse();
            r.success = false; r.message = "userId required";
            return ResponseEntity.badRequest().body(r);
        }
        LocalDate today = LocalDate.now();
        if (service.hasCheckedInToday(userId, today)) {
            CheckinResponse r = new CheckinResponse();
            r.success = false; r.message = "Đã điểm danh hôm nay";
            r.date = today;
            return ResponseEntity.ok(r);
        }
        service.checkIn(userId, today);
        CheckinResponse r = new CheckinResponse();
        r.success = true; r.message = "Điểm danh thành công"; r.date = today;
        return ResponseEntity.ok(r);
    }

    // GET /api/attendance/counts?userId=1
    @GetMapping("/counts")
    public ResponseEntity<CountsResponse> counts(@RequestParam Integer userId) {
        LocalDate today = LocalDate.now();
        CountsResponse r = new CountsResponse();
        r.weekCount = service.countThisWeek(userId, today);
        r.monthCount = service.countThisMonth(userId, today);
        return ResponseEntity.ok(r);
    }

    // GET /api/attendance/dates?userId=1  -> trả về list ngày đã điểm danh
    @GetMapping("/dates")
    public ResponseEntity<DatesResponse> dates(@RequestParam Integer userId) {
        List<LocalDate> list = service.getCheckinDates(userId);
        DatesResponse r = new DatesResponse();
        r.dates = list;
        return ResponseEntity.ok(r);
    }
}
