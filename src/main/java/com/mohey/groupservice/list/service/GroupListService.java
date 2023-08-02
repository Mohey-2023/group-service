package com.mohey.groupservice.list.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.list.dto.CalendarRequestDto;
import com.mohey.groupservice.list.dto.CalendarResponseDto;
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupApplicantRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;

@Service
public class GroupListService {
	private final GroupDetailRepository groupDetailRepository;
	private final GroupModifiableRepository groupModifiableRepository;
	private final GroupTagRepository groupTagRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final CategoryRepository categoryRepository;
	private final GenderOptionsRepository genderOptionsRepository;
	private final GroupApplicantRepository groupApplicantRepository;

	@Autowired
	public GroupListService(GroupDetailRepository groupDetailRepository,
		GroupModifiableRepository groupModifiableRepository,
		GroupTagRepository groupTagRepository,
		GroupParticipantRepository groupParticipantRepository,
		CategoryRepository categoryRepository,
		GenderOptionsRepository genderOptionsRepository,
		GroupApplicantRepository groupApplicantRepository
	){
		this.groupDetailRepository = groupDetailRepository;
		this.groupModifiableRepository = groupModifiableRepository;
		this.groupTagRepository = groupTagRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.categoryRepository = categoryRepository;
		this.genderOptionsRepository = genderOptionsRepository;
		this.groupApplicantRepository = groupApplicantRepository;
	}

	public List<GroupEntity> getMemberGroupList(String memberUuid){
		List<GroupParticipantEntity> memberParticipantList = groupParticipantRepository
			.findByMemberUuidAndGroupParticipantStatusIsNull(memberUuid);

		return memberParticipantList.stream()
			.map(GroupParticipantEntity::getGroupId)
			.map(groupDetailRepository::findByGroupId)
			.collect(Collectors.toList());
	}

	public List<CalendarResponseDto> getCalendarGroupList(CalendarRequestDto calendarRequestDto){
		List<GroupEntity> memberGroupList = getMemberGroupList(calendarRequestDto.getMemberUuid());

		return memberGroupList.stream()
			.map(groupEntity -> {
				CalendarResponseDto calendarResponseDto = new CalendarResponseDto();
				calendarResponseDto.setGroupUuid(groupEntity.getGroupUuid());

				GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
					.findLatestGroupModifiableByGroupId(groupEntity.getId());
				calendarResponseDto.setGroupStartDatetime(groupModifiableEntity.getGroupStartDatetime());
				calendarResponseDto.setTitle(groupModifiableEntity.getTitle());
				calendarResponseDto.setLat(groupModifiableEntity.getLat());
				calendarResponseDto.setLng(groupModifiableEntity.getLng());
				calendarResponseDto.setLocationId(groupModifiableEntity.getLocationId());

				// 카테고리 설정 해야됨


				return calendarResponseDto;
			})
			.collect(Collectors.toList());
	}
}
