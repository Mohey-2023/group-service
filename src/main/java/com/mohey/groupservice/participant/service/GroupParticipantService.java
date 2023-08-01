package com.mohey.groupservice.participant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.repository.GroupParticipantRepository;

@Service
public class GroupParticipantService {

    private final GroupParticipantRepository groupParticipantRepository;

    @Autowired
    public GroupParticipantService(GroupParticipantRepository groupParticipantRepository) {
        this.groupParticipantRepository = groupParticipantRepository;
    }

    // 그룹 참여하기
    // public void joinGroup(Long groupId, String participantId) {
    //     GroupParticipantEntity groupParticipant = new GroupParticipantEntity();
    // }

    // 그룹 나가기
    // public void leaveGroup(Long groupId, String participantId) {
    //
    // }
}