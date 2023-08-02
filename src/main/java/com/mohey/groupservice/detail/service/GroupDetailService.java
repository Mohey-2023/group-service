package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.entity.category.TagEntity;
import com.mohey.groupservice.entity.group.GroupConfirmEntity;
import com.mohey.groupservice.entity.group.GroupCoordinatesEntity;
import com.mohey.groupservice.detail.dto.GroupDto;
import com.mohey.groupservice.entity.group.GroupDeleteEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.group.GroupTagEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupCoordinatesRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupDetailService {
    private final GroupDetailRepository groupDetailRepository;
    private final GroupModifiableRepository groupModifiableRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupCoordinatesRepository groupCoordinatesRepository;
    private final GroupParticipantRepository groupParticipantRepository;
    private final CategoryRepository categoryRepository;
    private final GenderOptionsRepository genderOptionsRepository;


    @Autowired
    public GroupDetailService(GroupDetailRepository groupDetailRepository,
        GroupModifiableRepository groupModifiableRepository,
        GenderOptionsRepository genderOptionsRepository,
        GroupTagRepository groupTagRepository,
        GroupCoordinatesRepository groupCoordinatesRepository,
        GroupParticipantRepository groupParticipantRepository,
        CategoryRepository categoryRepository){
        this.groupDetailRepository = groupDetailRepository;
        this.groupModifiableRepository = groupModifiableRepository;
        this.groupTagRepository = groupTagRepository;
        this.groupCoordinatesRepository = groupCoordinatesRepository;
        this.groupParticipantRepository = groupParticipantRepository;
        this.categoryRepository = categoryRepository;
        this.genderOptionsRepository = genderOptionsRepository;
    }

    public  GroupDto getGroupDetailByGroupId(String groupId) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupId);
        GroupDto group = new GroupDto();
        if (groupEntity != null) {
            GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
                .findLatestGroupModifiableByGroupId(groupEntity.getId());

            GroupCoordinatesEntity groupCoordinatesEntity = groupCoordinatesRepository
                .findByGroupTbIdAndCreatedDatetime(groupEntity.getId(), groupModifiableEntity.getCreatedDatetime());

            List<GroupParticipantEntity> groupParticipantEntities = groupParticipantRepository
                .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());

            group.setGroupId(groupEntity.getId());
            group.setCategory(categoryRepository.findById(groupModifiableEntity.getCategoryTbId()).getCategoryName());
            group.setGenderOptions(genderOptionsRepository.findById(groupModifiableEntity.getGenderOptionsTbId()).getGenderDescription());
            group.setParticipantsNum(groupParticipantEntities.size());
            group.setGroupDescription(groupModifiableEntity.getDescription());
            group.setGroupStartDatetime(groupModifiableEntity.getGroupStartDatetime());
            group.setMaxParticipant(groupModifiableEntity.getMaxParticipant());
            group.setLeaderUuid(groupModifiableEntity.getLeaderUuid());
            group.setLocationId(groupCoordinatesEntity.getLocationId());
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

    public void setGroupPublicStatus(){

    }
}