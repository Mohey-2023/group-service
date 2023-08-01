package com.mohey.groupservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.participant.GroupParticipantEntity;

@Repository
public interface GroupParticipantRepository extends JpaRepository<GroupParticipantEntity, Long> {
	@Query("SELECT gp FROM GroupParticipantEntity gp LEFT JOIN gp.groupParticipantStatusEntity ps WHERE gp.groupId = :groupId AND (ps.createdDatetime IS NULL OR ps.createdDatetime = '')")
	List<GroupParticipantEntity> findByGroupIdAndGroupParticipantStatusIsNull(@Param("groupId") Long groupId);
}