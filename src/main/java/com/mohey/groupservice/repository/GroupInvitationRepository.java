package com.mohey.groupservice.repository;

import com.mohey.groupservice.entity.group.GroupInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitationEntity, Long> {
    List<GroupInvitationEntity> findByGroupUuidAnAndInviterMemberUuid(String groupUuid, String inviterMemberUuid);
}
