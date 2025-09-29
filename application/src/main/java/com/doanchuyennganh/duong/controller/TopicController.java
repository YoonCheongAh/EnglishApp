package com.doanchuyennganh.duong.controller;

import com.doanchuyennganh.duong.dto.TopicRequest;
import com.doanchuyennganh.duong.dto.TopicResponse;
import com.doanchuyennganh.duong.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Topic API")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả topics")
    public List<TopicResponse> getAllTopics() {
        return topicService.getAllTopics();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy topic theo ID")
    public TopicResponse getTopicById(@PathVariable Long id) {
        return topicService.getTopicById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo topic mới")
    public TopicResponse createTopic(@RequestBody TopicRequest request) {
        return topicService.createTopic(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật topic")
    public TopicResponse updateTopic(@PathVariable Long id, @RequestBody TopicRequest request) {
        return topicService.updateTopic(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa topic")
    public void deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
    }
}
