package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.entity.category.TagEntity;
import com.mohey.groupservice.entity.group.GroupCoordinatesEntity;
import com.mohey.groupservice.detail.dto.GroupDto;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.group.GroupTagEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.repository.GroupCoordinatesRepository;
import com.mohey.groupservice.repository.GroupDescriptionRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;

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

    public  GroupDto getGroupDetailByGroupId(String groupId) {
        GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupId);
        System.out.println(groupEntity);
        GroupDto group = new GroupDto();
        if (groupEntity != null) {
            GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
                .findLatestGroupModifiableByGroupId(groupEntity.getId());

            // List<GroupTagEntity> groupTagEntities = groupTagRepository
            //     .findByGroupTbIdAndCreatedDatetime(groupEntity.getId(), groupEntity.getCreatedDatetime());

            GroupCoordinatesEntity groupCoordinatesEntity = groupCoordinatesRepository
                .findByGroupTbIdAndCreatedDatetime(groupEntity.getId(), groupModifiableEntity.getCreatedDatetime());

            List<GroupParticipantEntity> groupParticipantEntities = groupParticipantRepository
                .findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());

            // List<String> tagNames = groupTagEntities.stream()
            //     .map(GroupTagEntity::getTagEntity)
            //     .map(TagEntity::getTagName)
            //     .collect(Collectors.toList());

            group.setGroupUuid(groupEntity.getGroupUuid());
            group.setParticipantsNum(groupParticipantEntities.size());
            group.setGroupDescription(groupModifiableEntity.getDescription());
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
            // group.setTags(tagNames);
        }
        return group;
    }
}