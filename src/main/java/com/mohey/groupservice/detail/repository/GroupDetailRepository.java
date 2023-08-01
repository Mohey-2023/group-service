package com.mohey.groupservice.detail.repository;


import com.mohey.groupservice.detail.model.group.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupDetailRepository extends JpaRepository<GroupEntity, Long> {
    GroupEntity findByGroupId(Long groupId);


}
