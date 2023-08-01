package com.mohey.groupservice.detail.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.detail.model.group.GroupDescriptionEntity;
import com.mohey.groupservice.detail.model.group.GroupTagEntity;

@Repository
public interface GroupTagRepository extends JpaRepository<GroupDescriptionEntity, Long> {

	List<GroupTagEntity> findByGroupDescriptionTbIdAndCreatedDatetime(Long groupDescriptionTbId, LocalDateTime createdDatetime);
}
