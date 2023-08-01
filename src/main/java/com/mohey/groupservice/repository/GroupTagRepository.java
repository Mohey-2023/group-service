package com.mohey.groupservice.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.group.GroupTagEntity;

@Repository
public interface GroupTagRepository extends JpaRepository<GroupTagEntity, Long> {

	List<GroupTagEntity> findByGroupDescriptionTbIdAndCreatedDatetime(Long groupDescriptionTbId, LocalDateTime createdDatetime);
}
