package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.detail.dto.GroupDto;
import com.mohey.groupservice.detail.dto.PublicStatusDto;
import com.mohey.groupservice.entity.group.GroupDeleteEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        GroupDto group = new GroupDto();
        if (groupEntity != null) {
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
            group.setGroupDescription(groupModifiableEntity.getDescription());
            group.setGroupStartDatetime(groupModifiableEntity.getGroupStartDatetime());
            group.setMaxParticipant(groupModifiableEntity.getMaxParticipant());
            group.setLeaderUuid(groupModifiableEntity.getLeaderUuid());
            group.setLocationName(groupModifiableEntity.getLocationName());
            group.setLocationAddress(groupModifiableEntity.getLocationAddress());
            group.setLat(groupModifiableEntity.getLat());
            group.setLng(groupModifiableEntity.getLng());
            group.setMinAge(groupModifiableEntity.getMinAge());
            group.setMaxAge(groupModifiableEntity.getMaxAge());
        }
        return group;
    }

    public void deleteGroup(String groupUuid){
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupUuid);

        GroupDeleteEntity deleteEntity = new GroupDeleteEntity();
        deleteEntity.setCreatedDatetime(LocalDateTime.now());
        deleteEntity.setId(groupEntity.getId());
        groupDeleteRepository.save(deleteEntity);
        groupEntity.setGroupDelete(deleteEntity);
        groupDetailRepository.save(groupEntity);
    }

    public void setGroupPublicStatus(PublicStatusDto publicStatus){
        GroupParticipantPublicStatusEntity status = new GroupParticipantPublicStatusEntity();

        status.setGroupParticipantId(groupParticipantRepository
            .findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupDetailRepository
                    .findByGroupUuid(publicStatus.getGroupUuid()).getId(),
                publicStatus.getMemberUuid()).getId());
        status.setCreatedDatetime(LocalDateTime.now());
        status.setStatus(publicStatus.getPublicYn());

        groupParticipantPublicStatusRepository.save(status);
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
        participants.forEach(participant -> {
            GroupParticipantStatusEntity status = new GroupParticipantStatusEntity();
            status.setId(participant.getId());
            status.setCreatedDatetime(LocalDateTime.now());
            participant.setGroupParticipantStatusEntity(status);
            groupParticipantStatusRepository.save(status);
            groupParticipantRepository.save(participant);
        });
    }
}