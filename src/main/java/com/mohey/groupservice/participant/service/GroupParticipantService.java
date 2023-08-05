package com.mohey.groupservice.participant.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.detail.service.GroupDetailService;
import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
import com.mohey.groupservice.leader.dto.leader.DelegateDto;
import com.mohey.groupservice.leader.service.GroupLeaderService;
import com.mohey.groupservice.participant.dto.JoinLeaveDto;
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupApplicantRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;

@Service
public class GroupParticipantService {
    private final GroupDetailRepository groupDetailRepository;
    private final GroupModifiableRepository groupModifiableRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupParticipantRepository groupParticipantRepository;
    private final CategoryRepository categoryRepository;
    private final GenderOptionsRepository genderOptionsRepository;
    private final GroupApplicantRepository groupApplicantRepository;
    private final GroupLeaderService groupLeaderService;
    private final GroupDetailService groupDetailService;

    @Autowired
    public GroupParticipantService(GroupDetailRepository groupDetailRepository,
        GroupModifiableRepository groupModifiableRepository,
        GroupTagRepository groupTagRepository,
        GroupParticipantRepository groupParticipantRepository,
        CategoryRepository categoryRepository,
        GroupApplicantRepository groupApplicantRepository,
        GenderOptionsRepository genderOptionsRepository,
        GroupLeaderService groupLeaderService,
        GroupDetailService groupDetailService
    ){
        this.groupDetailRepository = groupDetailRepository;
        this.groupModifiableRepository = groupModifiableRepository;
        this.groupTagRepository = groupTagRepository;
        this.groupParticipantRepository = groupParticipantRepository;
        this.categoryRepository = categoryRepository;
        this.genderOptionsRepository = genderOptionsRepository;
        this.groupApplicantRepository = groupApplicantRepository;
        this.groupLeaderService = groupLeaderService;
        this.groupDetailService = groupDetailService;
    }


    public void joinGroup(JoinLeaveDto joinLeaveDto) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(joinLeaveDto.getGroupUuid());

        GroupApplicantEntity groupApplicant = new GroupApplicantEntity();

        groupApplicant.setGroupId(groupEntity.getId());
        groupApplicant.setMemberUuid(joinLeaveDto.getMemberUuid());
        groupApplicant.setCreatedDatetime(LocalDateTime.now());

        groupApplicantRepository.save(groupApplicant);

    }

    public void leaveGroup(JoinLeaveDto joinLeaveDto) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(joinLeaveDto.getGroupUuid());

        GroupParticipantEntity leavingMember = groupParticipantRepository
            .findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(),
                joinLeaveDto.getMemberUuid());

        GroupParticipantStatusEntity status = new GroupParticipantStatusEntity();

        status.setId(leavingMember.getId());
        status.setCreatedDatetime(LocalDateTime.now());
        leavingMember.setGroupParticipantStatusEntity(status);

        groupParticipantRepository.save(leavingMember);

        GroupModifiableEntity groupModifiableEntity = groupModifiableRepository.findLatestGroupModifiableByGroupId(
            groupEntity.getId());
        List<GroupParticipantEntity> participantList = groupParticipantRepository
            .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());
        if(joinLeaveDto.getMemberUuid().equals(groupModifiableEntity.getLeaderUuid())){
            if(participantList.size() > 0) {
                DelegateDto dto = new DelegateDto();
                dto.setGroupUuid(groupEntity.getGroupUuid());
                dto.setLeaderUuid(joinLeaveDto.getMemberUuid());
                dto.setDelegatedUuid(participantList.get(0).getMemberUuid());
                groupLeaderService.delegateLeadership(dto);
            } else {
                groupDetailService.deleteGroup(groupEntity.getGroupUuid());
            }
        } else {
            if(participantList.size() == 0) {
                groupDetailService.deleteGroup(groupEntity.getGroupUuid());
            }
        }


        // chat한테 groupuuid, memberuuid
    }
}