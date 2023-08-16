package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.detail.dto.*;
import com.mohey.groupservice.entity.category.TagEntity;
import com.mohey.groupservice.entity.group.GroupDeleteEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupInvitationEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
import com.mohey.groupservice.exception.GroupNotFoundException;
import com.mohey.groupservice.interprocess.client.ChatFeginClient;
import com.mohey.groupservice.interprocess.client.FeignClient;
import com.mohey.groupservice.interprocess.dto.*;
import com.mohey.groupservice.kafka.KafkaProducer;
import com.mohey.groupservice.participant.dto.DeletedGroupsParticipantsDto;
import com.mohey.groupservice.repository.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupDetailService {
    private final GroupDetailRepository groupDetailRepository;
    private final GroupModifiableRepository groupModifiableRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupParticipantRepository groupParticipantRepository;
    private final CategoryRepository categoryRepository;
    private final GenderOptionsRepository genderOptionsRepository;
    private final GroupParticipantStatusRepository groupParticipantStatusRepository;
    private final GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository;
    private final GroupDeleteRepository groupDeleteRepository;
    private final ChatFeginClient chatFeginClient;
    private final FeignClient feignClient;
    private final KafkaProducer kafkaProducer;
    private final TagRepository tagRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupRealtimeRepository groupRealtimeRepository;
    private final GroupConfirmRepository groupConfirmRepository;
    private final GroupApplicantRepository groupApplicantRepository;


    @Autowired
    public GroupDetailService(GroupDetailRepository groupDetailRepository,
                              GroupModifiableRepository groupModifiableRepository,
                              GenderOptionsRepository genderOptionsRepository,
                              GroupTagRepository groupTagRepository,
                              GroupParticipantRepository groupParticipantRepository,
                              CategoryRepository categoryRepository,
                              GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository,
                              GroupDeleteRepository groupDeleteRepository,
                              GroupParticipantStatusRepository groupParticipantStatusRepository,
                              ChatFeginClient chatFeginClient,
                              FeignClient feignClient,
                              KafkaProducer kafkaProducer,
                              TagRepository tagRepository,
                              GroupInvitationRepository groupInvitationRepository,
                              GroupRealtimeRepository groupRealtimeRepository,
                              GroupConfirmRepository groupConfirmRepository,
        GroupApplicantRepository groupApplicantRepository) {
        this.groupDetailRepository = groupDetailRepository;
        this.groupModifiableRepository = groupModifiableRepository;
        this.groupTagRepository = groupTagRepository;
        this.groupParticipantRepository = groupParticipantRepository;
        this.categoryRepository = categoryRepository;
        this.genderOptionsRepository = genderOptionsRepository;
        this.groupParticipantPublicStatusRepository = groupParticipantPublicStatusRepository;
        this.groupDeleteRepository = groupDeleteRepository;
        this.groupParticipantStatusRepository = groupParticipantStatusRepository;
        this.chatFeginClient = chatFeginClient;
        this.feignClient = feignClient;
        this.kafkaProducer = kafkaProducer;
        this.tagRepository = tagRepository;
        this.groupInvitationRepository = groupInvitationRepository;
        this.groupRealtimeRepository = groupRealtimeRepository;
        this.groupConfirmRepository = groupConfirmRepository;
        this.groupApplicantRepository = groupApplicantRepository;
    }

    public GroupDto getGroupDetailByGroupId(String groupId, String memberUuid) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupId);

        if (groupEntity == null) {
            throw new GroupNotFoundException("잘못된 접근입니다.");
        }

        GroupDto group = new GroupDto();
        GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
                .findLatestGroupModifiableByGroupId(groupEntity.getId());

        List<GroupParticipantEntity> groupParticipantEntities = groupParticipantRepository
                .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());

        group.setGroupUuid(groupEntity.getGroupUuid());
        categoryRepository.findById(groupModifiableEntity.getCategoryTbId())
                .ifPresent(category -> group.setCategory(category.getCategoryName()));

        genderOptionsRepository.findById(groupModifiableEntity.getGenderOptionsTbId())
                .ifPresent(genderOptions -> group.setGenderOptions(genderOptions.getGenderDescription()));
        group.setParticipantsNum(groupParticipantEntities.size());
        group.setTitle(groupModifiableEntity.getTitle());
        group.setDescription(groupModifiableEntity.getDescription());
        group.setStartDatetime(groupModifiableEntity.getGroupStartDatetime());
        group.setMaxParticipant(groupModifiableEntity.getMaxParticipant());
        group.setLeaderName(feignClient.getMemberName(groupModifiableEntity.getLeaderUuid()).getMemberName());
        group.setLocationName(groupModifiableEntity.getLocationName());
        group.setLocationAddress(groupModifiableEntity.getLocationAddress());
        group.setLat(groupModifiableEntity.getLat());
        group.setLng(groupModifiableEntity.getLng());
        group.setTags(groupTagRepository.findByGroupModifiableTbId(groupModifiableEntity.getId())
                .stream()
                .map(groupTagEntity -> {
                    TagEntity tagEntity = tagRepository.findById(groupTagEntity.getTagTbId()).orElse(null);
                    if (tagEntity != null) {
                        return tagEntity.getTagName();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        if(groupParticipantRepository
                .findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), memberUuid) != null){
            group.setIsMember(true);
        } else {
            group.setIsMember(false);
        }

        if(groupModifiableEntity.getLeaderUuid().equals(memberUuid)){
            group.setIsLeader(true);
        } else {
            group.setIsLeader(false);
        }

        if(groupApplicantRepository
            .findByGroupIdAndMemberUuidApplicantsWithNoStatus(groupEntity.getId(), memberUuid) != null){
            group.setIsApplicant(true);
        } else {
            group.setIsApplicant(false);
        }

        if(groupRealtimeRepository.existsById(groupEntity.getId())){
            group.setIsRealtimePossible(true);
        } else {
            group.setIsRealtimePossible(false);
        }

        if(groupConfirmRepository.existsById(groupEntity.getId())){
            group.setIsConfirmed(true);
        } else {
            group.setIsConfirmed(false);
        }

        return group;
    }

    public void deleteGroup(String groupUuid) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupUuid);

        GroupDeleteEntity deleteEntity = GroupDeleteEntity.builder()
                .createdDatetime(LocalDateTime.now())
                .id(groupEntity.getId())
                .build();

        groupDeleteRepository.save(deleteEntity);
    }

    public void setGroupPublicStatus(PublicStatusDto publicStatus) {
        GroupParticipantPublicStatusEntity status = GroupParticipantPublicStatusEntity.builder()
                .groupParticipantId(groupParticipantRepository.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(
                        groupDetailRepository.findByGroupUuid(publicStatus.getGroupUuid()).getId(),
                        publicStatus.getMemberUuid()).getId())
                .createdDatetime(LocalDateTime.now())
                .status(publicStatus.getPublicYn())
                .build();

        groupParticipantPublicStatusRepository.save(status);
    }

    public static int calculateAge(LocalDateTime birthDate) {
        LocalDateTime currentDate = LocalDateTime.now();
        int age = currentDate.getYear() - birthDate.getYear();

        int month1 = currentDate.getMonthValue();
        int month2 = birthDate.getMonthValue();
        int day1 = currentDate.getDayOfMonth();
        int day2 = birthDate.getDayOfMonth();

        if (month1 < month2 || (month1 == month2 && day1 < day2)) {
            age--;
        }

        return age;
    }

    public GroupParticipantListDto getGroupParticipantList(String groupUuid) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupUuid);

        if (groupEntity == null) {
            throw new GroupNotFoundException("잘못된 접근입니다.");
        }

        GroupModifiableEntity modifiable = groupModifiableRepository.findLatestGroupModifiableByGroupId(groupEntity.getId());

        GroupParticipantListDto participantList = new GroupParticipantListDto();

        List<GroupParticipantDto> participants = groupParticipantRepository
                .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId())
                .stream()
                .map(groupParticipantEntity -> {
                    GroupParticipantDto participantDto = new GroupParticipantDto();
                    participantDto.setMemberUuid(groupParticipantEntity.getMemberUuid());
                    // 유저랑 통신해서 프사랑 즐찾 가져와야됨
                    MemberGroupDetailCommunicationDto groupDetailCommunicationDto = feignClient.getProfilePicture(groupParticipantEntity.getMemberUuid()).getMemberDetailList();

                    participantDto.setAge(calculateAge(groupDetailCommunicationDto.getBirthDate()));
                    participantDto.setMemberName(groupDetailCommunicationDto.getMemberName());
                    participantDto.setMemberGender(groupDetailCommunicationDto.getMemberGender());
                    participantDto.setProfilePicture(groupDetailCommunicationDto.getProfilePicture());

                    if(modifiable.getLeaderUuid().equals(groupParticipantEntity.getMemberUuid())){
                        participantDto.setIsLeader(true);
                    } else {
                        participantDto.setIsLeader(false);
                    }

                    return participantDto;
                }).collect(Collectors.toList());
        participantList.setParticipants(participants);
        participantList.setGroupUuid(groupUuid);

        return participantList;
    }

    public void deleteNotConfirmedGroups(LocalDateTime startTime, LocalDateTime endTime) {
        List<GroupEntity> groupsToBeDeleted = groupDetailRepository.findGroupsToBeDeleted(startTime, endTime);

        groupsToBeDeleted.stream()
            .forEach(groupEntity -> {
                    deleteGroup(groupEntity.getGroupUuid());

                    List<GroupParticipantEntity> participants = groupParticipantRepository
                            .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());

                    DeletedGroupsParticipantsDto dto = new DeletedGroupsParticipantsDto();
                    dto.setParticipants(participants);
                    dto.setGroupUuid(groupEntity.getGroupUuid());

                kickEverybody(dto);
                });
    }

    public void kickEverybody(DeletedGroupsParticipantsDto dto) {

        dto.getParticipants().forEach(participant -> {
            ChatCommunicationDto chatCommunicationDto = new ChatCommunicationDto();
            chatCommunicationDto.setGroupUuid(dto.getGroupUuid());
            chatCommunicationDto.setMemberUuid(participant.getMemberUuid());
            chatFeginClient.exit(chatCommunicationDto);

            GroupParticipantStatusEntity status = GroupParticipantStatusEntity.builder()
                    .createdDatetime(LocalDateTime.now())
                .id(participant.getId())
                    .build();


            GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(dto.getGroupUuid());

            MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(
                participant.getMemberUuid());
            MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
            memberNotificationDetailDto.setReceiverUuid(participant.getMemberUuid());
            memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
            memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
            List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
            memberNotificationList.add(memberNotificationDetailDto);
            GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
            groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
            groupNotificationDetailDto.setGroupName(groupModifiableRepository
                .findLatestGroupModifiableByGroupId(groupEntity.getId()).getTitle());
            GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
            groupNotificationDto.setTopic("group-kick");
            groupNotificationDto.setType("group");
            groupNotificationDto.setSenderUuid("");
            groupNotificationDto.setSenderName("");
            groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
            groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
            kafkaProducer.send("group-kick", groupNotificationDto);

            groupParticipantStatusRepository.save(status);
        });
    }

    public void inviteFriend(GroupInviteDto groupInviteDto){
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupInviteDto.getGroupUuid());

        GroupInvitationEntity groupInvitation = GroupInvitationEntity.builder()
                .groupUuid(groupInviteDto.getGroupUuid())
                .inviterMemberUuid(groupInviteDto.getSenderUuid())
                .invitedMemberUuid(groupInviteDto.getReceiverUuid())
                .invitationTime(LocalDateTime.now())
                .build();

        groupInvitationRepository.save(groupInvitation);

        MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(groupInviteDto.getReceiverUuid());
        MemberNotificationResponseDto requestSenderDto = feignClient.getMemberNotificationDetail(groupInviteDto.getSenderUuid());
        MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
        memberNotificationDetailDto.setReceiverUuid(groupInviteDto.getReceiverUuid());
        memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
        memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
        List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
        memberNotificationList.add(memberNotificationDetailDto);
        GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
        groupNotificationDetailDto.setGroupUuid(groupInviteDto.getGroupUuid());
        groupNotificationDetailDto.setGroupName(groupModifiableRepository
                .findLatestGroupModifiableByGroupId(groupEntity.getId()).getTitle());
        GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
        groupNotificationDto.setTopic("group-invite");
        groupNotificationDto.setType("group");
        groupNotificationDto.setSenderUuid(groupInviteDto.getSenderUuid());
        groupNotificationDto.setSenderName(requestSenderDto.getReceiverName());
        groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
        groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
        kafkaProducer.send("group-invite", groupNotificationDto);
    }

    public List<String> getInvitedHistory(String groupUuid, String memberUuid){
        return groupInvitationRepository
                .findByGroupUuidAndInviterMemberUuid(groupUuid, memberUuid)
                .stream()
                .map(groupInvitationEntity -> {
                    return groupInvitationEntity.getInvitedMemberUuid();
                })
                .collect(Collectors.toList());
    }
}