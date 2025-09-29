package com.doanchuyennganh.duong.service;

import com.doanchuyennganh.duong.dto.TopicRequest;
import com.doanchuyennganh.duong.dto.TopicResponse;
import com.doanchuyennganh.duong.model.Topic;
import com.doanchuyennganh.duong.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TopicResponse getTopicById(Long id) {
        return topicRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }

    public TopicResponse createTopic(TopicRequest request) {
        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        return mapToResponse(topicRepository.save(topic));
    }

    public TopicResponse updateTopic(Long id, TopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        return mapToResponse(topicRepository.save(topic));
    }

    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }

    private TopicResponse mapToResponse(Topic topic) {
        TopicResponse res = new TopicResponse();
        res.setTopicId(topic.getTopicId());
        res.setName(topic.getName());
        res.setDescription(topic.getDescription());
        return res;
    }
}

