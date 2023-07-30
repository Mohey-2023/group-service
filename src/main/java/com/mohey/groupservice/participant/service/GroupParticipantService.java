package com.mohey.groupservice.participant.service;

import com.mohey.groupservice.detail.model.participant.GroupParticipantEntity;
import com.mohey.groupservice.participant.repository.GroupParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupParticipantService {

    private final GroupParticipantRepository groupParticipantRepository;

    @Autowired
    public GroupParticipantService(GroupParticipantRepository groupParticipantRepository) {
        this.groupParticipantRepository = groupParticipantRepository;
    }

    // 그룹 참여하기
    public void joinGroup(Long groupId, String participantId) {
        GroupParticipantEntity groupParticipant = new GroupParticipantEntity();
        groupParticipant.setGroupId(groupId);
        groupParticipant.setMemberUuid(participantId);
        groupParticipantRepository.save(groupParticipant);
    }

    // 그룹 나가기
    public void leaveGroup(Long groupId, String participantId) {

    }
}