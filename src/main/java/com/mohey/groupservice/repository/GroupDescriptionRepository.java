package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupDescriptionRepository extends JpaRepository<GroupDescriptionEntity, Long> {

	GroupDescriptionEntity findFirstByGroupModifiableTbIdOrderByCreatedDatetimeDesc(Long groupModifiableTbId);
}
