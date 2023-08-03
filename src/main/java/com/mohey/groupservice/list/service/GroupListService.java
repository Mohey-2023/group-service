package com.mohey.groupservice.list.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.list.dto.CalendarRequestDto;
import com.mohey.groupservice.list.dto.CalendarResponseDto;
import com.mohey.groupservice.list.dto.MyGroupListMainPageDto;
import com.mohey.groupservice.list.dto.MyGroupListMyPageDto;
import com.mohey.groupservice.list.dto.YourGroupListDto;
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupApplicantRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantPublicStatusRepository;
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
	private final GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository;

	@Autowired
	public GroupListService(GroupDetailRepository groupDetailRepository,
		GroupModifiableRepository groupModifiableRepository,
		GroupTagRepository groupTagRepository,
		GroupParticipantRepository groupParticipantRepository,
		CategoryRepository categoryRepository,
		GenderOptionsRepository genderOptionsRepository,
		GroupApplicantRepository groupApplicantRepository,
		GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository
	){
		this.groupDetailRepository = groupDetailRepository;
		this.groupModifiableRepository = groupModifiableRepository;
		this.groupTagRepository = groupTagRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.categoryRepository = categoryRepository;
		this.genderOptionsRepository = genderOptionsRepository;
		this.groupApplicantRepository = groupApplicantRepository;
		this.groupParticipantPublicStatusRepository = groupParticipantPublicStatusRepository;
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
		List<GroupEntity> groupList = groupDetailRepository
			.findGroupsByYearAndMonthForParticipant(calendarRequestDto.getYear(), calendarRequestDto.getMonth(), calendarRequestDto.getMemberUuid());

		return groupList.stream()
			.map(groupEntity -> {
				CalendarResponseDto calendarResponseDto = new CalendarResponseDto();
				calendarResponseDto.setGroupUuid(groupEntity.getGroupUuid());
				calendarResponseDto.setGroupStartDatetime(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime());
				calendarResponseDto.setTitle(groupEntity.getGroupModifiableList().get(0).getTitle());
				calendarResponseDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
				calendarResponseDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
				calendarResponseDto.setLocationId(groupEntity.getGroupModifiableList().get(0).getLocationId());
				calendarResponseDto.setCategory(categoryRepository.findById(groupEntity.getGroupModifiableList().get(0).getCategoryTbId()).getCategoryName());

				return calendarResponseDto;
			})
			.collect(Collectors.toList());
	}

	public List<MyGroupListMainPageDto> getMyMainPageGroupList(String memberUuid){
		List<GroupEntity> futureGroupList = groupDetailRepository
			.findFutureConfirmedGroupsForParticipant(memberUuid, LocalDateTime.now());

		return futureGroupList.stream()
			.map(groupEntity -> {
				MyGroupListMainPageDto myGroupListMainPageDto = new MyGroupListMainPageDto();

				myGroupListMainPageDto.setGroupUuid(groupEntity.getGroupUuid());
				myGroupListMainPageDto.setTitle(groupEntity.getGroupModifiableList().get(0).getTitle());
				myGroupListMainPageDto
					.setCategory(categoryRepository
						.findById(groupEntity
							.getGroupModifiableList()
							.get(0)
							.getCategoryTbId())
						.getCategoryName());
				myGroupListMainPageDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
				myGroupListMainPageDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
				myGroupListMainPageDto.setLocationId(groupEntity.getGroupModifiableList().get(0).getLocationId());
				myGroupListMainPageDto.setParticipantNum(groupEntity.getGroupParticipantEntityList().size());
				myGroupListMainPageDto.setGroupStartDatetime(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime());
				Duration duration = Duration.between(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime(),LocalDateTime.now());
				myGroupListMainPageDto.setRemainingSecond(duration.getSeconds());

				// 멤버랑 통신해서 프사 가져와야됨
				// myGroupListMainPageDto.setProfilePicture1();
				// myGroupListMainPageDto.setProfilePicture2();
				// myGroupListMainPageDto.setProfilePicture3();


				return myGroupListMainPageDto;
			})
			.collect(Collectors.toList());
	}

	public List<MyGroupListMyPageDto> getMyPageGroupList(String memberUuid){
		List<GroupEntity> myGroupList = groupDetailRepository.findAllGroupsForParticipant(memberUuid);

		return myGroupList.stream()
			.map(groupEntity -> {
				MyGroupListMyPageDto myGroupListMyPageDto = new MyGroupListMyPageDto();

				myGroupListMyPageDto.setGroupUuid(groupEntity.getGroupUuid());
				myGroupListMyPageDto.setTitle(groupEntity.getGroupModifiableList().get(0).getTitle());
				myGroupListMyPageDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
				myGroupListMyPageDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
				myGroupListMyPageDto.setLocationId(groupEntity.getGroupModifiableList().get(0).getLocationId());
				myGroupListMyPageDto
					.setCategory(categoryRepository
						.findById(groupEntity
							.getGroupModifiableList()
							.get(0)
							.getCategoryTbId())
						.getCategoryName());
				myGroupListMyPageDto.setGroupStartDatetime(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime());
				myGroupListMyPageDto.setIsPrivate(groupParticipantPublicStatusRepository
					.findFirstByGroupParticipantTbIdOrderByCreatedDatetimeDesc(groupParticipantRepository
						.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), memberUuid).getId())
					.getStatus());

				return myGroupListMyPageDto;
			})
			.collect(Collectors.toList());
	}

	public List<YourGroupListDto> getYourPageGroupList(String memberUuid){
		List<GroupEntity> yourGroupList = groupDetailRepository.findAllGroupsForParticipant(memberUuid);

		return yourGroupList.stream()
			.map(groupEntity -> {
				YourGroupListDto yourGroupListDto = new YourGroupListDto();

				Boolean status = groupParticipantPublicStatusRepository
					.findFirstByGroupParticipantTbIdOrderByCreatedDatetimeDesc(groupParticipantRepository
						.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), memberUuid).getId())
					.getStatus();
				if(!status){
					return null;
				}
				yourGroupListDto.setGroupUuid(groupEntity.getGroupUuid());
				yourGroupListDto.setTitle(groupEntity.getGroupModifiableList().get(0).getTitle());
				yourGroupListDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
				yourGroupListDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
				yourGroupListDto.setLocationId(groupEntity.getGroupModifiableList().get(0).getLocationId());
				yourGroupListDto.setGroupStartDatetime(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime());
				yourGroupListDto
					.setCategory(categoryRepository
						.findById(groupEntity.getGroupModifiableList().get(0).getCategoryTbId())
						.getCategoryName());


				return yourGroupListDto;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}
}
