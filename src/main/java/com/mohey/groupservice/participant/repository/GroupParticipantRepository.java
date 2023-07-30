package com.mohey.groupservice.participant.repository;

import com.mohey.groupservice.detail.model.participant.GroupParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupParticipantRepository extends JpaRepository<GroupParticipantEntity, Long> {
}