package com.mohey.groupservice.repository;

import com.mohey.groupservice.entity.group.GroupConfirmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupConfirmRepository extends JpaRepository<GroupConfirmEntity, Long> {
}
