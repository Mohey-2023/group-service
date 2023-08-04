package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.group.GroupDeleteEntity;

@Repository
public interface GroupDeleteRepository extends JpaRepository<GroupDeleteEntity, Long> {
}
