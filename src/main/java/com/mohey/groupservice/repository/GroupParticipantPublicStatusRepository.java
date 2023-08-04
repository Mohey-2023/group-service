package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;

@Repository
public interface GroupParticipantPublicStatusRepository extends JpaRepository<GroupParticipantPublicStatusEntity, Long> {
	GroupParticipantPublicStatusEntity findFirstByGroupParticipantIdOrderByCreatedDatetimeDesc(Long groupParticipantId);
}
