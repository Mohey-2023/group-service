package com.mohey.groupservice.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.group.GroupCoordinatesEntity;

@Repository
public interface GroupCoordinatesRepository extends JpaRepository<GroupCoordinatesEntity, Long> {
	GroupCoordinatesEntity findByGroupTbIdAndCreatedDatetime(Long groupTbId, LocalDateTime createdTime);
}
