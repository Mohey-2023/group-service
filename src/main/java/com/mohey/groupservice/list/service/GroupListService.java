package com.mohey.groupservice.list.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import com.mohey.groupservice.entity.group.GroupModifiableEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.interprocess.client.FeignClient;
import com.mohey.groupservice.interprocess.dto.MemberFriendListResponseDto;
import com.mohey.groupservice.list.dto.CalendarRequestDto;
import com.mohey.groupservice.list.dto.CalendarResponseDto;
import com.mohey.groupservice.list.dto.FriendGroupDto;
import com.mohey.groupservice.list.dto.FriendListDto;
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
	private final FeignClient feignClient;

	@Autowired
	public GroupListService(GroupDetailRepository groupDetailRepository,
		GroupModifiableRepository groupModifiableRepository,
		GroupTagRepository groupTagRepository,
		GroupParticipantRepository groupParticipantRepository,
		CategoryRepository categoryRepository,
		GenderOptionsRepository genderOptionsRepository,
		GroupApplicantRepository groupApplicantRepository,
		GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository,
		FeignClient feignClient
	) {
		this.groupDetailRepository = groupDetailRepository;
		this.groupModifiableRepository = groupModifiableRepository;
		this.groupTagRepository = groupTagRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.categoryRepository = categoryRepository;
		this.genderOptionsRepository = genderOptionsRepository;
		this.groupApplicantRepository = groupApplicantRepository;
		this.groupParticipantPublicStatusRepository = groupParticipantPublicStatusRepository;
		this.feignClient = feignClient;
	}

	public List<GroupEntity> getMemberGroupList(String memberUuid) {
		return groupDetailRepository
			.findAllGroupsForParticipant(memberUuid);
	}

	public List<CalendarResponseDto> getCalendarGroupList(CalendarRequestDto calendarRequestDto) {
		List<GroupEntity> groupList = groupDetailRepository
			.findGroupsByYearAndMonthForParticipant(calendarRequestDto.getYear(), calendarRequestDto.getMonth(),
				calendarRequestDto.getMemberUuid());

		return groupList.stream()
			.map(groupEntity -> {
				CalendarResponseDto calendarResponseDto = new CalendarResponseDto();
				calendarResponseDto.setGroupUuid(groupEntity.getGroupUuid());

				GroupModifiableEntity modifiable = groupModifiableRepository.findLatestGroupModifiableByGroupId(
					groupEntity.getId());

				calendarResponseDto.setGroupStartDatetime(modifiable.getGroupStartDatetime());
				calendarResponseDto.setTitle(modifiable.getTitle());
				calendarResponseDto.setLat(modifiable.getLat());
				calendarResponseDto.setLng(modifiable.getLng());
				calendarResponseDto.setLocationAddress(modifiable.getLocationAddress());

				categoryRepository.findById(modifiable.getCategoryTbId())
					.ifPresent(category -> calendarResponseDto.setCategory(category.getCategoryName()));

				return calendarResponseDto;
			})
			.collect(Collectors.toList());
	}

	public List<MyGroupListMainPageDto> getMyMainPageGroupList(String memberUuid) {
		List<GroupEntity> futureGroupList = groupDetailRepository
			.findFutureConfirmedGroupsForParticipant(memberUuid, LocalDateTime.now());

		return futureGroupList.stream()
			.map(groupEntity -> {
				MyGroupListMainPageDto myGroupListMainPageDto = new MyGroupListMainPageDto();

				myGroupListMainPageDto.setGroupUuid(groupEntity.getGroupUuid());
				GroupModifiableEntity modifiable = groupModifiableRepository.findLatestGroupModifiableByGroupId(
					groupEntity.getId());

				categoryRepository
					.findById(modifiable
						.getCategoryTbId())
					.ifPresent(category -> myGroupListMainPageDto
						.setCategory(category
							.getCategoryName()));

				myGroupListMainPageDto.setTitle(modifiable.getTitle());
				myGroupListMainPageDto.setLng(modifiable.getLng());
				myGroupListMainPageDto.setLat(modifiable.getLat());
				myGroupListMainPageDto.setLocationAddress(modifiable.getLocationAddress());
				myGroupListMainPageDto.setParticipantNum(
					groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId())
						.size());
				myGroupListMainPageDto.setGroupStartDatetime(modifiable.getGroupStartDatetime());
				Duration duration = Duration.between(LocalDateTime.now(), modifiable.getGroupStartDatetime());
				myGroupListMainPageDto.setRemainingSecond(duration.getSeconds());

				List<GroupParticipantEntity> groupParticipantEntities = groupParticipantRepository
					.findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId());

				List<GroupParticipantEntity> selectedParticipants = new ArrayList<>();
				int numToShow = Math.min(groupParticipantEntities.size(), 2); // 최대 2명까지 표시

				if (numToShow == 1) {
					selectedParticipants.add(groupParticipantEntities.get(0));
				} else if (numToShow > 1) {
					Random random = new Random();
					List<GroupParticipantEntity> remainingParticipants = new ArrayList<>(groupParticipantEntities);

					for (int i = 0; i < numToShow; i++) {
						int randomIndex = random.nextInt(remainingParticipants.size());
						selectedParticipants.add(remainingParticipants.get(randomIndex));
						remainingParticipants.remove(randomIndex);
					}
				}

				return myGroupListMainPageDto;
			})
			.collect(Collectors.toList());
	}

	public List<MyGroupListMyPageDto> getMyPageGroupList(String memberUuid) {
		List<GroupEntity> myGroupList = groupDetailRepository.findAllGroupsForParticipant(memberUuid);

		return myGroupList.stream()
			.map(groupEntity -> {
				MyGroupListMyPageDto myGroupListMyPageDto = new MyGroupListMyPageDto();

				myGroupListMyPageDto.setGroupUuid(groupEntity.getGroupUuid());
				GroupModifiableEntity modifiable = groupModifiableRepository.findLatestGroupModifiableByGroupId(
					groupEntity.getId());
				myGroupListMyPageDto.setTitle(modifiable.getTitle());
				myGroupListMyPageDto.setLng(modifiable.getLng());
				myGroupListMyPageDto.setLat(modifiable.getLat());
				myGroupListMyPageDto.setLocationAddress(modifiable.getLocationAddress());

				categoryRepository
					.findById(modifiable.getCategoryTbId())
					.ifPresent(category -> myGroupListMyPageDto
						.setCategory(category
							.getCategoryName()));
				myGroupListMyPageDto.setGroupStartDatetime(modifiable.getGroupStartDatetime());
				myGroupListMyPageDto.setIsPrivate(groupParticipantPublicStatusRepository
					.findFirstByGroupParticipantIdOrderByCreatedDatetimeDesc(groupParticipantRepository
						.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), memberUuid)
						.getId())
					.getStatus());

				return myGroupListMyPageDto;
			})
			.collect(Collectors.toList());
	}

	public List<YourGroupListDto> getYourPageGroupList(String memberUuid) {
		List<GroupEntity> yourGroupList = groupDetailRepository.findAllGroupsForParticipant(memberUuid);

		return yourGroupList.stream()
			.map(groupEntity -> {
				YourGroupListDto yourGroupListDto = new YourGroupListDto();

				Boolean status = groupParticipantPublicStatusRepository
					.findFirstByGroupParticipantIdOrderByCreatedDatetimeDesc(groupParticipantRepository
						.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), memberUuid)
						.getId())
					.getStatus();

				GroupModifiableEntity modifiable = groupModifiableRepository.findLatestGroupModifiableByGroupId(
					groupEntity.getId());
				if (!status || modifiable.getPrivateYn()) {
					return null;
				}
				yourGroupListDto.setGroupUuid(groupEntity.getGroupUuid());
				yourGroupListDto.setTitle(modifiable.getTitle());
				yourGroupListDto.setLng(modifiable.getLng());
				yourGroupListDto.setLat(modifiable.getLat());
				yourGroupListDto.setLocationAddress(modifiable.getLocationAddress());
				yourGroupListDto.setGroupStartDatetime(modifiable.getGroupStartDatetime());

				categoryRepository
					.findById(modifiable.getCategoryTbId())
					.ifPresent(category -> yourGroupListDto
						.setCategory(category
							.getCategoryName()));

				return yourGroupListDto;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public List<MapGroupListResponseDto> getMapGroupList(MapGroupListRequestDto mapGroupListRequestDto) {
		if (!mapGroupListRequestDto.getIsFriend()) {
			List<GroupEntity> mapGroupList = groupDetailRepository.findGroupsInMap(LocalDateTime.now(),
				mapGroupListRequestDto.getSwLng(), mapGroupListRequestDto.getSwLat(), mapGroupListRequestDto.getNeLng(),
				mapGroupListRequestDto.getNeLat(), mapGroupListRequestDto.getTitleKeyword(),
				mapGroupListRequestDto.getGenderOptionsUuid(), mapGroupListRequestDto.getCategoryUuid(),
				mapGroupListRequestDto.getMinAge(), mapGroupListRequestDto.getMaxAge());

			return mapGroupList.stream()
				.map(groupEntity -> {

					GroupModifiableEntity modifiable = groupModifiableRepository.findLatestGroupModifiableByGroupId(
						groupEntity.getId());

					if (modifiable.getPrivateYn()) {
						return null;
					}
					MapGroupListResponseDto mapGroupListResponseDto = new MapGroupListResponseDto();
					mapGroupListResponseDto.setGroupUuid(groupEntity.getGroupUuid());
					mapGroupListResponseDto.setMaxParticipantNum(modifiable.getMaxParticipant());
					mapGroupListResponseDto.setTitle(modifiable.getTitle());
					mapGroupListResponseDto.setParticipantNum(
						groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(groupEntity.getId())
							.size());
					mapGroupListResponseDto.setLng(modifiable.getLng());
					mapGroupListResponseDto.setLat(modifiable.getLat());
					mapGroupListResponseDto.setLocationAddress(modifiable.getLocationAddress());

					categoryRepository
						.findById(modifiable.getCategoryTbId())
						.ifPresent(category -> mapGroupListResponseDto
							.setCategory(category
								.getCategoryName()));
					mapGroupListResponseDto.setGroupStartDatetime(modifiable.getGroupStartDatetime());

					return mapGroupListResponseDto;
				}).filter(Objects::nonNull)
				.collect(Collectors.toList());
		} else {
			// 친구 리스트 가져와서 그거에 맞춰서 보낼 필요 있음
			return null;
		}
	}

	public List<FriendGroupDto> getFriendsGroupList(String memberUuid) {
		MemberFriendListResponseDto friendListResponse = feignClient.getFriendsList(memberUuid);

		List<String> friendsUuidList = friendListResponse.getFriendList();

		List<FriendListDto> friendGroups = new ArrayList<>();

		for (String friendUuid : friendsUuidList) {
			groupDetailRepository.findFutureNotConfirmedGroupsForParticipant(
				friendUuid, LocalDateTime.now()).forEach(groupEntity -> {

				FriendListDto friendListDto = new FriendListDto();
				friendListDto.setGroupEntity(groupEntity);
				friendListDto.setFriendUuid(friendUuid);
				friendGroups.add(friendListDto);
			});
		}

		if (friendGroups.size() >= 6) {
			Collections.shuffle(friendGroups);

			List<FriendGroupDto> result = friendGroups.stream()
				.distinct()
				.limit(6)
				.map(friendListDto -> {
					GroupEntity groupEntity = friendListDto.getGroupEntity();

					FriendGroupDto groupDto = new FriendGroupDto();
					GroupModifiableEntity modifiable = groupModifiableRepository
						.findLatestGroupModifiableByGroupId(groupEntity.getId());
					groupDto.setGroupUuid(groupEntity.getGroupUuid());

					groupDto.setName(feignClient.getMemberName(friendListDto.getFriendUuid()).getMemberName());
					categoryRepository
						.findById(modifiable.getCategoryTbId())
						.ifPresent(category -> groupDto
							.setCategory(category
								.getCategoryName()));
					groupDto.setTitle(modifiable.getTitle());
					groupDto.setGroupStartDatetime(modifiable.getGroupStartDatetime());
					groupDto.setLocationAddress(modifiable.getLocationAddress());
					groupDto.setMaxParticipantNum(modifiable.getMaxParticipant());
					groupDto.setParticipantNum(groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(
						groupEntity.getId()).size());
					return groupDto;
				})
				.collect(Collectors.toList());

			return result;
		} else {
			List<FriendGroupDto> result = friendGroups.stream()
				.map(friendListDto -> {
					GroupEntity groupEntity = friendListDto.getGroupEntity();

					FriendGroupDto groupDto = new FriendGroupDto();
					GroupModifiableEntity modifiable = groupModifiableRepository
						.findLatestGroupModifiableByGroupId(groupEntity.getId());
					groupDto.setGroupUuid(groupEntity.getGroupUuid());

					groupDto.setName(feignClient.getMemberName(friendListDto.getFriendUuid()).getMemberName());
					categoryRepository
						.findById(modifiable.getCategoryTbId())
						.ifPresent(category -> groupDto
							.setCategory(category
								.getCategoryName()));
					groupDto.setTitle(modifiable.getTitle());
					groupDto.setGroupStartDatetime(modifiable.getGroupStartDatetime());
					groupDto.setLocationAddress(modifiable.getLocationAddress());
					groupDto.setMaxParticipantNum(modifiable.getMaxParticipant());
					groupDto.setParticipantNum(groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(
						groupEntity.getId()).size());
					return groupDto;
				})
				.collect(Collectors.toList());

			return result;
		}
	}
}
