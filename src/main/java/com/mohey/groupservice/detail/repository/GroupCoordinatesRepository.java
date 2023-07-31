package com.mohey.groupservice.detail.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.detail.model.group.GroupCoordinatesEntity;
import com.mohey.groupservice.detail.model.group.GroupEntity;

@Repository
public interface GroupCoordinatesRepository extends JpaRepository<GroupEntity, Long> {
	GroupCoordinatesEntity findByGroupTbIdAndCreatedDatetime(Long groupTbId, LocalDateTime createdTime);
}
