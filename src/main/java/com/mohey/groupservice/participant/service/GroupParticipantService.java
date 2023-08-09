package com.mohey.groupservice.participant.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mohey.groupservice.interprocess.client.ChatFeginClient;
import com.mohey.groupservice.interprocess.client.FeignClient;
import com.mohey.groupservice.interprocess.dto.ChatCommunicationDto;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDetailDto;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationDetailDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationResponseDto;
import com.mohey.groupservice.kafka.KafkaProducer;
import com.mohey.groupservice.repository.*;
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

import lombok.RequiredArgsConstructor;

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
    private final GroupParticipantStatusRepository groupParticipantStatusRepository;
    private final FeignClient feignClient;
    private final KafkaProducer kafkaProducer;
    private final ChatFeginClient chatFeginClient;

    @Autowired
    public GroupParticipantService(GroupDetailRepository groupDetailRepository,
        GroupModifiableRepository groupModifiableRepository,
        GroupTagRepository groupTagRepository,
        GroupParticipantRepository groupParticipantRepository,
        CategoryRepository categoryRepository,
        GroupApplicantRepository groupApplicantRepository,
        GenderOptionsRepository genderOptionsRepository,
        GroupLeaderService groupLeaderService,
        GroupDetailService groupDetailService,
        GroupParticipantStatusRepository groupParticipantStatusRepository,
        FeignClient feignClient,
        KafkaProducer kafkaProducer,
        ChatFeginClient chatFeginClient
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
        this.groupParticipantStatusRepository = groupParticipantStatusRepository;
        this.feignClient = feignClient;
        this.kafkaProducer = kafkaProducer;
        this.chatFeginClient = chatFeginClient;
    }


    public void joinGroup(JoinLeaveDto joinLeaveDto) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(joinLeaveDto.getGroupUuid());

        GroupApplicantEntity groupApplicant = GroupApplicantEntity.builder()
                .groupId(groupEntity.getId())
                .memberUuid(joinLeaveDto.getMemberUuid())
                .createdDatetime(LocalDateTime.now())
                .build();

        GroupModifiableEntity modifiableEntity = groupModifiableRepository.findLatestGroupModifiableByGroupId(groupEntity.getId());


        groupApplicantRepository.save(groupApplicant);

        MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(modifiableEntity.getLeaderUuid());
        MemberNotificationResponseDto requestSenderDto = feignClient.getMemberNotificationDetail(joinLeaveDto.getMemberUuid());
        MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
        memberNotificationDetailDto.setReceiverUuid(modifiableEntity.getLeaderUuid());
        memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
        memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
        List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
        memberNotificationList.add(memberNotificationDetailDto);
        GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
        groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
        groupNotificationDetailDto.setGroupName(modifiableEntity.getTitle());
        GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
        groupNotificationDto.setTopic("group-join");
        groupNotificationDto.setType("group");
        groupNotificationDto.setSenderUuid(joinLeaveDto.getMemberUuid());
        groupNotificationDto.setSenderName(requestSenderDto.getReceiverName());
        groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
        groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
        kafkaProducer.send("group-join", groupNotificationDto);
    }

    public void leaveGroup(JoinLeaveDto joinLeaveDto) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(joinLeaveDto.getGroupUuid());

        GroupParticipantEntity leavingMember = groupParticipantRepository
                .findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(),
                        joinLeaveDto.getMemberUuid());

        GroupParticipantStatusEntity status = GroupParticipantStatusEntity.builder()
                .id(leavingMember.getId())
                .createdDatetime(LocalDateTime.now())
                .build();

        groupParticipantStatusRepository.save(status);

        GroupModifiableEntity groupModifiableEntity = groupModifiableRepository.findLatestGroupModifiableByGroupId(
                groupEntity.getId());
        List<GroupParticipantEntity> participantList = groupParticipantRepository
                .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());
        if (joinLeaveDto.getMemberUuid().equals(groupModifiableEntity.getLeaderUuid())) {
            if (participantList.size() > 0) {
                DelegateDto dto = new DelegateDto();
                dto.setGroupUuid(groupEntity.getGroupUuid());
                dto.setLeaderUuid(joinLeaveDto.getMemberUuid());
                dto.setDelegatedUuid(participantList.get(0).getMemberUuid());
                groupLeaderService.delegateLeadership(dto);
            } else {
                groupDetailService.deleteGroup(groupEntity.getGroupUuid());
            }
        } else {
            if (participantList.size() == 0) {
                groupDetailService.deleteGroup(groupEntity.getGroupUuid());
            }
        }

        ChatCommunicationDto chatCommunicationDto = new ChatCommunicationDto();
        chatCommunicationDto.setGroupUuid(joinLeaveDto.getGroupUuid());
        chatCommunicationDto.setMemberUuid(joinLeaveDto.getMemberUuid());

        chatFeginClient.create(chatCommunicationDto);
    }
}