package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.detail.dto.GroupDto;
import com.mohey.groupservice.detail.dto.GroupParticipantDto;
import com.mohey.groupservice.detail.dto.GroupParticipantListDto;
import com.mohey.groupservice.detail.dto.GroupParticipantRequestDto;
import com.mohey.groupservice.detail.dto.PublicStatusDto;
import com.mohey.groupservice.entity.group.GroupDeleteEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
import com.mohey.groupservice.exception.GroupNotFoundException;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDetailDto;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDto;
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



    @Autowired
    public GroupDetailService(GroupDetailRepository groupDetailRepository,
        GroupModifiableRepository groupModifiableRepository,
        GenderOptionsRepository genderOptionsRepository,
        GroupTagRepository groupTagRepository,
        GroupParticipantRepository groupParticipantRepository,
        CategoryRepository categoryRepository,
        GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository,
        GroupDeleteRepository groupDeleteRepository,
        GroupParticipantStatusRepository groupParticipantStatusRepository){
        this.groupDetailRepository = groupDetailRepository;
        this.groupModifiableRepository = groupModifiableRepository;
        this.groupTagRepository = groupTagRepository;
        this.groupParticipantRepository = groupParticipantRepository;
        this.categoryRepository = categoryRepository;
        this.genderOptionsRepository = genderOptionsRepository;
        this.groupParticipantPublicStatusRepository = groupParticipantPublicStatusRepository;
        this.groupDeleteRepository = groupDeleteRepository;
        this.groupParticipantStatusRepository = groupParticipantStatusRepository;
    }

    public  GroupDto getGroupDetailByGroupId(String groupId) {
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

    public void deleteGroup(String groupUuid){
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupUuid);

        GroupDeleteEntity deleteEntity = GroupDeleteEntity.builder()
                .createdDatetime(LocalDateTime.now())
                .id(groupEntity.getId())
                .build();

        groupDeleteRepository.save(deleteEntity);
    }

    public void setGroupPublicStatus(PublicStatusDto publicStatus){
        GroupParticipantPublicStatusEntity status = GroupParticipantPublicStatusEntity.builder()
                .id(groupParticipantRepository.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(
                        groupDetailRepository.findByGroupUuid(publicStatus.getGroupUuid()).getId(),
                        publicStatus.getMemberUuid()).getId())
                .createdDatetime(LocalDateTime.now())
                .status(publicStatus.getPublicYn())
                .build();

        groupParticipantPublicStatusRepository.save(status);
    }

    public GroupParticipantListDto getGroupParticipantList(GroupParticipantRequestDto groupParticipantRequestDto){
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
                    return participantDto;
                }).collect(Collectors.toList());
        participantList.setParticipants(participants);
        participantList.setGroupUuid(groupParticipantRequestDto.getGroupUuid());

        return participantList;
    }

    public void deleteNotConfirmedGroups(LocalDateTime oneHourBefore){
        List<GroupEntity> groupsToBeDeleted = groupDetailRepository.findGroupsToBeDeleted(oneHourBefore);

        groupsToBeDeleted.stream()
            .map(groupEntity -> {
                deleteGroup(groupEntity.getGroupUuid());

                List<GroupParticipantEntity> participants = groupParticipantRepository
                    .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());

                return participants;
            })
            .collect(Collectors.toList())
            .forEach(participantsList -> {
                kickEverybody(participantsList);
            });
    }

    public void kickEverybody(List<GroupParticipantEntity> participants){
        List<GroupParticipantStatusEntity> statuses = new ArrayList<>();

        participants.forEach(participant -> {
            GroupParticipantStatusEntity status = GroupParticipantStatusEntity.builder()
                    .createdDatetime(LocalDateTime.now())
                    .build();

            statuses.add(status);

            Optional<GroupEntity> group = groupDetailRepository.findById(participant.getId());

            GroupNotificationDetailDto notificationDetailDto = new GroupNotificationDetailDto();
            GroupNotificationDto notificationDto = new GroupNotificationDto();

            group.ifPresent(groupEntity -> {
                notificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
                notificationDetailDto.setGroupName(groupModifiableRepository
                    .findLatestGroupModifiableByGroupId(groupEntity.getId()).getTitle());

                notificationDto.setSenderUuid(groupEntity.getGroupUuid());
                notificationDto.setSenderName(groupModifiableRepository
                    .findLatestGroupModifiableByGroupId(groupEntity.getId()).getTitle());
            });
            notificationDto.setTopic("group-kick");
            notificationDto.setType("group");
            notificationDto.setGroupNotificationDetailDto(notificationDetailDto);

            // 유저
        });

        groupParticipantStatusRepository.saveAll(statuses);
    }
}