package com.mohey.groupservice.list.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.list.dto.CalendarRequestDto;
import com.mohey.groupservice.list.dto.CalendarResponseDto;
import com.mohey.groupservice.list.dto.MapGroupListRequestDto;
import com.mohey.groupservice.list.dto.MapGroupListResponseDto;
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
			.map(groupDetailRepository::findById)
			.flatMap(Optional::stream)
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
				calendarResponseDto.setLocationAddress(groupEntity.getGroupModifiableList().get(0).getLocationAddress());

				categoryRepository.findById(groupEntity.getGroupModifiableList().get(0).getCategoryTbId()).ifPresent(category -> calendarResponseDto.setCategory(category.getCategoryName()));

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

				categoryRepository
					.findById(groupEntity
						.getGroupModifiableList()
						.get(0).getCategoryTbId())
					.ifPresent(category -> myGroupListMainPageDto
						.setCategory(category
							.getCategoryName()));

				myGroupListMainPageDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
				myGroupListMainPageDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
				myGroupListMainPageDto.setLocationAddress(groupEntity.getGroupModifiableList().get(0).getLocationAddress());
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
				myGroupListMyPageDto.setLocationAddress(groupEntity.getGroupModifiableList().get(0).getLocationAddress());

				categoryRepository
					.findById(groupEntity
						.getGroupModifiableList()
						.get(0).getCategoryTbId())
					.ifPresent(category -> myGroupListMyPageDto
						.setCategory(category
							.getCategoryName()));
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
					if(!status || groupEntity.getGroupModifiableList().get(0).getPrivateYn()){
						return null;
					}
					yourGroupListDto.setGroupUuid(groupEntity.getGroupUuid());
					yourGroupListDto.setTitle(groupEntity.getGroupModifiableList().get(0).getTitle());
					yourGroupListDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
					yourGroupListDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
					yourGroupListDto.setLocationAddress(groupEntity.getGroupModifiableList().get(0).getLocationAddress());
					yourGroupListDto.setGroupStartDatetime(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime());

					categoryRepository
							.findById(groupEntity
									.getGroupModifiableList()
									.get(0).getCategoryTbId())
							.ifPresent(category -> yourGroupListDto
									.setCategory(category
											.getCategoryName()));

					return yourGroupListDto;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	public List<MapGroupListResponseDto> getMapGroupList(MapGroupListRequestDto mapGroupListRequestDto){
		List<GroupEntity> mapGroupList = groupDetailRepository.findGroupsInMap(LocalDateTime.now(),
			mapGroupListRequestDto.getSwlng(), mapGroupListRequestDto.getSwlat(), mapGroupListRequestDto.getNeLng(),
			mapGroupListRequestDto.getNeLat());

		return mapGroupList.stream()
			.map(groupEntity -> {
				if(groupEntity.getGroupModifiableList().get(0).getPrivateYn()){
					return null;
				}
				MapGroupListResponseDto mapGroupListResponseDto = new MapGroupListResponseDto();
				mapGroupListResponseDto.setGroupUuid(groupEntity.getGroupUuid());
				mapGroupListResponseDto.setMaxParticipantNum(groupEntity.getGroupModifiableList().get(0).getMaxParticipant());
				mapGroupListResponseDto.setTitle(groupEntity.getGroupModifiableList().get(0).getTitle());
				mapGroupListResponseDto.setParticipantNum(groupEntity.getGroupParticipantEntityList().size());
				mapGroupListResponseDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
				mapGroupListResponseDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
				mapGroupListResponseDto.setLocationAddress(groupEntity.getGroupModifiableList().get(0).getLocationAddress());


				categoryRepository
					.findById(groupEntity
						.getGroupModifiableList()
						.get(0).getCategoryTbId())
					.ifPresent(category -> mapGroupListResponseDto
						.setCategory(category
							.getCategoryName()));
				mapGroupListResponseDto.setGroupStartDatetime(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime());


				return mapGroupListResponseDto;
			}).filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public List<MapGroupListResponseDto> getMapFriendsGroupList(MapGroupListRequestDto mapGroupListRequestDto){
		List<GroupEntity> mapGroupList = groupDetailRepository.findGroupsInMap(LocalDateTime.now(),
				mapGroupListRequestDto.getSwlng(), mapGroupListRequestDto.getSwlat(), mapGroupListRequestDto.getNeLng(),
				mapGroupListRequestDto.getNeLat());

		// 친구 가져와서 친구의 그룹 보여줘야 함

		return mapGroupList.stream()
				.map(groupEntity -> {
					MapGroupListResponseDto mapGroupListResponseDto = new MapGroupListResponseDto();

					mapGroupListResponseDto.setGroupUuid(groupEntity.getGroupUuid());
					mapGroupListResponseDto.setMaxParticipantNum(groupEntity.getGroupModifiableList().get(0).getMaxParticipant());
					mapGroupListResponseDto.setTitle(groupEntity.getGroupModifiableList().get(0).getTitle());
					mapGroupListResponseDto.setParticipantNum(groupEntity.getGroupParticipantEntityList().size());
					mapGroupListResponseDto.setLng(groupEntity.getGroupModifiableList().get(0).getLng());
					mapGroupListResponseDto.setLat(groupEntity.getGroupModifiableList().get(0).getLat());
					mapGroupListResponseDto.setLocationAddress(groupEntity.getGroupModifiableList().get(0).getLocationAddress());

					categoryRepository
							.findById(groupEntity
									.getGroupModifiableList()
									.get(0).getCategoryTbId())
							.ifPresent(category -> mapGroupListResponseDto
									.setCategory(category
											.getCategoryName()));
					mapGroupListResponseDto.setGroupStartDatetime(groupEntity.getGroupModifiableList().get(0).getGroupStartDatetime());


					return mapGroupListResponseDto;
				})
				.collect(Collectors.toList());
	}
}
