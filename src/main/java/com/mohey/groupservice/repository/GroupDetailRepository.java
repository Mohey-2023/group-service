package com.mohey.groupservice.repository;


import com.mohey.groupservice.entity.group.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupDetailRepository extends JpaRepository<GroupEntity, Long> {
    GroupEntity findByGroupUuid(String groupUuid);


}
