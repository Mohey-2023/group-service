package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.detail.dto.GroupDto;
import com.mohey.groupservice.detail.dto.PublicStatusDto;
import com.mohey.groupservice.entity.group.GroupDeleteEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantPublicStatusRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GroupDetailService {
    private final GroupDetailRepository groupDetailRepository;
    private final GroupModifiableRepository groupModifiableRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupParticipantRepository groupParticipantRepository;
    private final CategoryRepository categoryRepository;
    private final GenderOptionsRepository genderOptionsRepository;
    private final GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository;


    @Autowired
    public GroupDetailService(GroupDetailRepository groupDetailRepository,
        GroupModifiableRepository groupModifiableRepository,
        GenderOptionsRepository genderOptionsRepository,
        GroupTagRepository groupTagRepository,
        GroupParticipantRepository groupParticipantRepository,
        CategoryRepository categoryRepository,
        GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository){
        this.groupDetailRepository = groupDetailRepository;
        this.groupModifiableRepository = groupModifiableRepository;
        this.groupTagRepository = groupTagRepository;
        this.groupParticipantRepository = groupParticipantRepository;
        this.categoryRepository = categoryRepository;
        this.genderOptionsRepository = genderOptionsRepository;
        this.groupParticipantPublicStatusRepository = groupParticipantPublicStatusRepository;
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

        groupEntity.setGroupDelete(deleteEntity);
        groupDetailRepository.save(groupEntity);
    }

    public void setGroupPublicStatus(PublicStatusDto publicStatus){
        GroupParticipantPublicStatusEntity status = new GroupParticipantPublicStatusEntity();

        status.setId(groupParticipantRepository
            .findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupDetailRepository
                    .findByGroupUuid(publicStatus.getGroupUuid()).getId(),
                publicStatus.getMemberUuid()).getId());
        status.setCreatedDatetime(LocalDateTime.now());
        status.setStatus(publicStatus.getPublicYn());

        groupParticipantPublicStatusRepository.save(status);
    }
}