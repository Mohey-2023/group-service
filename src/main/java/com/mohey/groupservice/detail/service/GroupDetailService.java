package com.mohey.groupservice.detail.service;


import com.mohey.groupservice.detail.model.group.GroupDto;
import com.mohey.groupservice.detail.model.group.GroupEntity;
import com.mohey.groupservice.detail.model.group.GroupTagEntity;
import com.mohey.groupservice.detail.repository.GroupDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupDetailService {
    private final GroupDetailRepository groupDetailRepository;

    @Autowired
    public GroupDetailService(GroupDetailRepository groupDetailRepository) {
        this.groupDetailRepository = groupDetailRepository;
    }

    public  GroupDto getGroupDetailByGroupId(Long groupId) {
        Optional groupDetailDto = groupDetailRepository.findByGroupIdAndLatestTrue(groupId);

        GroupDto group = new GroupDto();

        if (groupDetailDto.isPresent()) {
            GroupEntity groupEntity = (GroupEntity) groupDetailDto.get();

            group.setGroupDescription(groupEntity.getGroupDescription());
            group.setGroupUuid(groupEntity.getGroupUuid());
            group.setGroupStartDatetime(groupEntity.getGroupStartDatetime());
            group.setCategory(groupEntity.getCategory().getCategoryName());

//            List<String> tags = groupEntity.getGroupTags().stream()
//                    .map(groupTagEntity -> groupTagEntity.getTagTbId().getTagName())
//                    .collect(Collectors.toList());
//            group.setTags(tags);

            group.setLat(groupEntity.getLat());
            group.setLng(groupEntity.getLng());
            group.setGenderOptions(groupEntity.getGenderOptions().getGenderDescription());
            group.setLeaderUuid(groupEntity.getLeaderUuid());
            group.setLocationId(groupEntity.getGroupCoordinate().getLocationId());
            group.setMaxAge(groupEntity.getMaxAge());
            group.setMinAge(groupEntity.getMinAge());
            group.setMaxParticipant(groupEntity.getMaxParticipant());
            group.setParticipantsNum(groupEntity.getGroupParticipants().size());
        }

        return group;
    }
}