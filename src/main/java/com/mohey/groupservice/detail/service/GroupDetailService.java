package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.detail.dto.*;
import com.mohey.groupservice.entity.group.GroupDeleteEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
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
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupDeleteRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantPublicStatusRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupParticipantStatusRepository;
import com.mohey.groupservice.repository.GroupTagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
                              KafkaProducer kafkaProducer) {
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
    }

    public GroupDto getGroupDetailByGroupId(String groupId) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupId);

        if (groupEntity == null) {
            throw new GroupNotFoundException("잘못된 접근입니다.");
        }

        GroupDto group = new GroupDto();
        GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
                .findLatestGroupModifiableByGroupId(groupEntity.getId());

        List<GroupParticipantEntity> groupParticipantEntities = groupParticipantRepository
                .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());

        group.setGroupId(groupEntity.getId());
        categoryRepository.findById(groupModifiableEntity.getCategoryTbId())
                .ifPresent(category -> group.setCategory(category.getCategoryName()));

        genderOptionsRepository.findById(groupModifiableEntity.getGenderOptionsTbId())
                .ifPresent(genderOptions -> group.setGenderOptions(genderOptions.getGenderDescription()));
        group.setParticipantsNum(groupParticipantEntities.size());
        group.setTitle(groupModifiableEntity.getTitle());
        group.setDescription(groupModifiableEntity.getDescription());
        group.setGroupStartDatetime(groupModifiableEntity.getGroupStartDatetime());
        group.setMaxParticipant(groupModifiableEntity.getMaxParticipant());
        group.setLeaderUuid(groupModifiableEntity.getLeaderUuid());
        group.setLocationName(groupModifiableEntity.getLocationName());
        group.setLocationAddress(groupModifiableEntity.getLocationAddress());
        group.setLat(groupModifiableEntity.getLat());
        group.setLng(groupModifiableEntity.getLng());
        group.setMinAge(groupModifiableEntity.getMinAge());
        group.setMaxAge(groupModifiableEntity.getMaxAge());

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
                .id(groupParticipantRepository.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(
                        groupDetailRepository.findByGroupUuid(publicStatus.getGroupUuid()).getId(),
                        publicStatus.getMemberUuid()).getId())
                .createdDatetime(LocalDateTime.now())
                .status(publicStatus.getPublicYn())
                .build();

        groupParticipantPublicStatusRepository.save(status);
    }

    public GroupParticipantListDto getGroupParticipantList(GroupParticipantRequestDto groupParticipantRequestDto) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupParticipantRequestDto.getGroupUuid());

        if (groupEntity == null) {
            throw new GroupNotFoundException("잘못된 접근입니다.");
        }

        GroupParticipantListDto participantList = new GroupParticipantListDto();

        List<GroupParticipantDto> participants = groupParticipantRepository
                .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId())
                .stream()
                .map(groupParticipantEntity -> {
                    GroupParticipantDto participantDto = new GroupParticipantDto();
                    participantDto.setMemberUuid(groupParticipantEntity.getMemberUuid());
                    // 유저랑 통신해서 프사랑 즐찾 가져와야됨
                    MemberGroupDetailCommunicationDto groupDetailCommunicationDto = feignClient.getProfilePicture(groupParticipantEntity.getMemberUuid()).getMemberDetailList();
                    participantDto.setBirthDate(groupDetailCommunicationDto.getBirthDate());
                    participantDto.setMemberName(groupDetailCommunicationDto.getMemberName());
                    participantDto.setMemberGender(groupDetailCommunicationDto.getMemberGender());
                    participantDto.setProfilePicture(groupDetailCommunicationDto.getProfilePicture());

                    return participantDto;
                }).collect(Collectors.toList());
        participantList.setParticipants(participants);
        participantList.setGroupUuid(groupParticipantRequestDto.getGroupUuid());

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
            chatFeginClient.create(chatCommunicationDto);

            GroupParticipantStatusEntity status = GroupParticipantStatusEntity.builder()
                    .createdDatetime(LocalDateTime.now())
                .id(participant.getId())
                    .build();


            GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(dto.getGroupUuid());

            MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(dto.getGroupUuid());
            MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
            memberNotificationDetailDto.setReceiverUuid(dto.getGroupUuid());
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

    public List<MemberFriendDetailListDto> getFriendsList(String memberUuid) {
        MemberFriendDetailListResponseDto memberFriendDetailListResponseDto = feignClient.getFriendsDetailList(memberUuid);

        return memberFriendDetailListResponseDto.getMemberFriendDetailList();
    }

    public List<MemberFriendDetailListDto> getFriendsListBySearch(String memberUuid, String keyword){
        MemberFriendDetailListResponseDto memberFriendDetailListResponseDto = feignClient.getFriendsDetailListBySearch(memberUuid, keyword);

        return memberFriendDetailListResponseDto.getMemberFriendDetailList();
    }

    public void inviteFriend(GroupInviteDto groupInviteDto){
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupInviteDto.getGroupUuid());
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
}