package com.mohey.groupservice.detail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.detail.model.group.GroupEntity;
import com.mohey.groupservice.detail.model.participant.GroupParticipantEntity;

@Repository
public interface GroupParticipantRepository extends JpaRepository<GroupEntity, Long> {
	List<GroupParticipantEntity> findByGroupIdAndParticipantStatusListIsNull(Long groupId);
}
