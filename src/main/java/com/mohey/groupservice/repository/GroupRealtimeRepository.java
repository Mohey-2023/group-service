package com.mohey.groupservice.repository;

import com.mohey.groupservice.entity.group.GroupRealtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRealtimeRepository extends JpaRepository<GroupRealtimeEntity, Long> {
}
