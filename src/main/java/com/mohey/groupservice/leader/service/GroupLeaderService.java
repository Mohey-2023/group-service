package com.mohey.groupservice.leader.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mohey.groupservice.leader.dto.leader.DelegateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.entity.category.CategoryEntity;
import com.mohey.groupservice.entity.group.GenderOptionsEntity;
import com.mohey.groupservice.entity.group.GroupCoordinatesEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.leader.dto.leader.CreateGroupDto;
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupCoordinatesRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;
@Service
public class GroupLeaderService {
	private final GroupDetailRepository groupDetailRepository;
	private final GroupModifiableRepository groupModifiableRepository;
	private final GroupTagRepository groupTagRepository;
	private final GroupCoordinatesRepository groupCoordinatesRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final CategoryRepository categoryRepository;
	private final GenderOptionsRepository genderOptionsRepository;

	@Autowired
	public GroupLeaderService(GroupDetailRepository groupDetailRepository,
		GroupModifiableRepository groupModifiableRepository,
		GroupTagRepository groupTagRepository,
		GroupCoordinatesRepository groupCoordinatesRepository,
		GroupParticipantRepository groupParticipantRepository,
		CategoryRepository categoryRepository,
		GenderOptionsRepository genderOptionsRepository
		){
		this.groupDetailRepository = groupDetailRepository;
		this.groupModifiableRepository = groupModifiableRepository;
		this.groupTagRepository = groupTagRepository;
		this.groupCoordinatesRepository = groupCoordinatesRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.categoryRepository = categoryRepository;
		this.genderOptionsRepository = genderOptionsRepository;
	}

	public void createGroup(CreateGroupDto groupDto){
		GroupEntity groupEntity = new GroupEntity();
		groupEntity.setGroupUuid(UUID.randomUUID().toString());
		groupDetailRepository.save(groupEntity);

		GroupModifiableEntity latest = groupModifiableRepository.findLatestGroupModifiableByGroupId(groupEntity.getId());
		if(latest != null) {
			latest.setLatestYn(false);
		}

		GroupModifiableEntity groupModifiableEntity = new GroupModifiableEntity();
		groupModifiableEntity.setGroupTbId(groupEntity.getId());

		CategoryEntity categoryEntity = categoryRepository.findByCategoryUuid(groupDto.getCategoryUuid());
		groupModifiableEntity.setCategoryTbId(categoryEntity.getId());

		GenderOptionsEntity genderOptionsEntity = genderOptionsRepository.findByGenderUuid(groupDto.getGenderOptionsUuid());
		groupModifiableEntity.setGenderOptionsTbId(genderOptionsEntity.getId());

		groupModifiableEntity.setTitle(groupDto.getTitle());
		groupModifiableEntity.setGroupStartDatetime(groupDto.getGroupStartDatetime());
		groupModifiableEntity.setMaxParticipant(groupDto.getMaxParticipant());
		groupModifiableEntity.setLeaderUuid(groupDto.getLeaderUuid());
		groupModifiableEntity.setPrivateYn(groupDto.isPrivacyYn());
		groupModifiableEntity.setLat(groupDto.getLat());
		groupModifiableEntity.setLng(groupDto.getLng());
		groupModifiableEntity.setMinAge(groupDto.getMinAge());
		groupModifiableEntity.setMaxAge(groupDto.getMaxAge());
		groupModifiableEntity.setDescription(groupDto.getDescription());
		groupModifiableEntity.setLatestYn(true);
		groupModifiableRepository.save(groupModifiableEntity);

		if(groupDto.getLocationId() != null){
			GroupCoordinatesEntity groupCoordinatesEntity = new GroupCoordinatesEntity();
			groupCoordinatesEntity.setGroupTbId(groupEntity.getId());
			groupCoordinatesEntity.setLocationId(groupDto.getLocationId());
			groupCoordinatesRepository.save(groupCoordinatesEntity);
		}
	}

	public void delegateLeadership(DelegateDto delegateDto){
		GroupModifiableEntity latest = groupModifiableRepository
				.findLatestGroupModifiableByGroupId(delegateDto.getGroupId());
		latest.setLatestYn(false);

		GroupModifiableEntity groupModifiableEntity = new GroupModifiableEntity();
		groupModifiableEntity = latest;

		groupModifiableEntity.setLeaderUuid(delegateDto.getDelegatedUuid());
		groupModifiableEntity.setLatestYn(true);
		groupModifiableEntity.setCreatedDatetime(null);

		groupModifiableRepository.save(groupModifiableEntity);
	}
}
