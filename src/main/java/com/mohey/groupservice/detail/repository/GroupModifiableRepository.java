package com.mohey.groupservice.detail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.detail.model.group.GroupModifiableEntity;

@Repository
public interface GroupModifiableRepository extends JpaRepository<GroupModifiableEntity, Long> {

	@Query("SELECT gm FROM GroupModifiableEntity gm " +
		"WHERE gm.groupTbId = :groupId AND gm.latestYn = true")
	GroupModifiableEntity findLatestGroupModifiableByGroupId(@Param("groupId") Long groupId);
}