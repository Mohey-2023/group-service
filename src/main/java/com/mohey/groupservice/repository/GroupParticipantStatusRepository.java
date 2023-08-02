package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;

@Repository
public interface GroupParticipantStatusRepository extends JpaRepository<GroupParticipantStatusEntity, Long> {
}
