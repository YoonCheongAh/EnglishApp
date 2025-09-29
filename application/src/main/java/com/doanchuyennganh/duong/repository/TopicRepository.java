package com.doanchuyennganh.duong.repository;

import com.doanchuyennganh.duong.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndTopicIdNot(String name, Long topicId);
}