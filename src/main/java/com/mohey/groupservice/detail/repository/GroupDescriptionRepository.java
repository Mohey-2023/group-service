package com.mohey.groupservice.detail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.detail.model.group.GroupDescriptionEntity;
import com.mohey.groupservice.detail.model.group.GroupModifiableEntity;

@Repository
public interface GroupDescriptionRepository extends JpaRepository<GroupModifiableEntity, Long> {

	GroupDescriptionEntity findFirstByGroupModifiableTbIdOrderByCreatedDatetimeDesc(Long groupModifiableId);
}
