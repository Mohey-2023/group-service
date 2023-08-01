package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.detail.model.category.TagEntity;
import com.mohey.groupservice.detail.model.group.GroupCoordinatesEntity;
import com.mohey.groupservice.detail.model.group.GroupDescriptionEntity;
import com.mohey.groupservice.detail.model.group.GroupDto;
import com.mohey.groupservice.detail.model.group.GroupEntity;
import com.mohey.groupservice.detail.model.group.GroupModifiableEntity;
import com.mohey.groupservice.detail.model.group.GroupTagEntity;
import com.mohey.groupservice.detail.model.participant.GroupParticipantEntity;
import com.mohey.groupservice.detail.repository.GroupCoordinatesRepository;
import com.mohey.groupservice.detail.repository.GroupDescriptionRepository;
import com.mohey.groupservice.detail.repository.GroupDetailRepository;
import com.mohey.groupservice.detail.repository.GroupModifiableRepository;
import com.mohey.groupservice.detail.repository.GroupParticipantRepository;
import com.mohey.groupservice.detail.repository.GroupTagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupDetailService {
    private final GroupDetailRepository groupDetailRepository;
    private final GroupModifiableRepository groupModifiableRepository;
    private final GroupDescriptionRepository groupDescriptionRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupCoordinatesRepository groupCoordinatesRepository;
    private final GroupParticipantRepository groupParticipantRepository;

    @Autowired
    public GroupDetailService(GroupDetailRepository groupDetailRepository,
        GroupModifiableRepository groupModifiableRepository,
        GroupDescriptionRepository groupDescriptionRepository,
        GroupTagRepository groupTagRepository,
        GroupCoordinatesRepository groupCoordinatesRepository,
        GroupParticipantRepository groupParticipantRepository){
        this.groupDetailRepository = groupDetailRepository;
        this.groupModifiableRepository = groupModifiableRepository;
        this.groupDescriptionRepository = groupDescriptionRepository;
        this.groupTagRepository = groupTagRepository;
        this.groupCoordinatesRepository = groupCoordinatesRepository;
        this.groupParticipantRepository = groupParticipantRepository;
    }

    public  GroupDto getGroupDetailByGroupId(Long groupId) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupId(groupId);
        GroupDto group = new GroupDto();
        if (groupEntity != null) {
            GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
                .findLatestGroupModifiableByGroupId(groupId);

            GroupDescriptionEntity groupDescriptionEntity = groupDescriptionRepository
                .findFirstByGroupModifiableTbIdOrderByCreatedDatetimeDesc(groupModifiableEntity.getId());

            List<GroupTagEntity> groupTagEntities = groupTagRepository
                .findByGroupDescriptionTbIdAndCreatedDatetime(groupDescriptionEntity.getId(), groupDescriptionEntity.getCreatedDatetime());

            GroupCoordinatesEntity groupCoordinatesEntity = groupCoordinatesRepository
                .findByGroupTbIdAndCreatedDatetime(groupId, groupModifiableEntity.getCreatedDatetime());

            List<GroupParticipantEntity> groupParticipantEntities = groupParticipantRepository
                .findByGroupIdAndParticipantStatusListIsNull(groupId);

            List<String> tagNames = groupTagEntities.stream()
                .map(GroupTagEntity::getTagEntity)
                .map(TagEntity::getTagName)
                .collect(Collectors.toList());

            group.setGroupUuid(groupEntity.getGroupUuid());
            group.setParticipantsNum(groupParticipantEntities.size());
            group.setGroupDescription(groupDescriptionEntity.getDescription());
            group.setCategory(groupModifiableEntity.getCategory().getCategoryName());
            group.setGroupStartDatetime(groupModifiableEntity.getGroupStartDatetime());
            group.setMaxParticipant(groupModifiableEntity.getMaxParticipant());
            group.setLeaderUuid(groupModifiableEntity.getLeaderUuid());
            group.setLocationId(groupCoordinatesEntity.getLocationId());
            group.setLat(groupModifiableEntity.getLat());
            group.setLng(groupModifiableEntity.getLng());
            group.setGenderOptions(groupModifiableEntity.getGenderOptions().getGenderDescription());
            group.setMinAge(groupModifiableEntity.getMinAge());
            group.setMaxAge(groupModifiableEntity.getMaxAge());
            group.setTags(tagNames);
        }
        return group;
    }
}