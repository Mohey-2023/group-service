package com.mohey.groupservice.leader.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;
import com.mohey.groupservice.entity.applicant.GroupApplicantStatusEntity;
import com.mohey.groupservice.entity.category.TagEntity;
import com.mohey.groupservice.entity.group.*;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
import com.mohey.groupservice.exception.NotGroupLeaderException;
import com.mohey.groupservice.interprocess.client.ChatFeginClient;
import com.mohey.groupservice.interprocess.client.FeignClient;
import com.mohey.groupservice.interprocess.dto.ChatCommunicationDto;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDetailDto;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDto;
import com.mohey.groupservice.interprocess.dto.MemberDetailResponseDto;
import com.mohey.groupservice.interprocess.dto.MemberGroupDetailCommunicationDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationDetailDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationResponseDto;
import com.mohey.groupservice.kafka.KafkaProducer;
import com.mohey.groupservice.leader.dto.applicant.ApplicantAcceptRejectDto;
import com.mohey.groupservice.leader.dto.applicant.GroupApplicantDto;
import com.mohey.groupservice.leader.dto.applicant.GroupApplicantListDto;
import com.mohey.groupservice.leader.dto.leader.DelegateDto;
import com.mohey.groupservice.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.leader.dto.leader.CreateGroupDto;
import com.mohey.groupservice.leader.dto.leader.GroupLeaderDto;
import com.mohey.groupservice.leader.dto.leader.KickDto;
import com.mohey.groupservice.leader.dto.leader.ModifyGroupDto;

@Service
public class GroupLeaderService {
	private final GroupDetailRepository groupDetailRepository;
	private final GroupModifiableRepository groupModifiableRepository;
	private final GroupConfirmRepository groupConfirmRepository;
	private final GroupTagRepository groupTagRepository;
	private final TagRepository tagRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final CategoryRepository categoryRepository;
	private final GenderOptionsRepository genderOptionsRepository;
	private final GroupApplicantRepository groupApplicantRepository;
	private final GroupApplicantStatusRepository groupApplicantStatusRepository;
	private final GroupParticipantStatusRepository groupParticipantStatusRepository;
	private final GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository;
	private final FeignClient feignClient;
	private final KafkaProducer kafkaProducer;
	private final ChatFeginClient chatFeginClient;
	private final GroupRealtimeRepository groupRealtimeRepository;

	@Autowired
	public GroupLeaderService(GroupDetailRepository groupDetailRepository,
		GroupModifiableRepository groupModifiableRepository,
		GroupTagRepository groupTagRepository,
		GroupParticipantRepository groupParticipantRepository,
		CategoryRepository categoryRepository,
		GenderOptionsRepository genderOptionsRepository,
		GroupApplicantRepository groupApplicantRepository,
		GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository,
		GroupApplicantStatusRepository groupApplicantStatusRepository,
		GroupParticipantStatusRepository groupParticipantStatusRepository,
		GroupConfirmRepository groupConfirmRepository,
		TagRepository tagRepository,
		FeignClient feignClient,
		KafkaProducer kafkaProducer,
		ChatFeginClient chatFeginClient,
							  GroupRealtimeRepository groupRealtimeRepository
	) {
		this.groupDetailRepository = groupDetailRepository;
		this.groupModifiableRepository = groupModifiableRepository;
		this.groupTagRepository = groupTagRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.categoryRepository = categoryRepository;
		this.genderOptionsRepository = genderOptionsRepository;
		this.groupApplicantRepository = groupApplicantRepository;
		this.groupParticipantPublicStatusRepository = groupParticipantPublicStatusRepository;
		this.groupApplicantStatusRepository = groupApplicantStatusRepository;
		this.groupParticipantStatusRepository = groupParticipantStatusRepository;
		this.groupConfirmRepository = groupConfirmRepository;
		this.tagRepository = tagRepository;
		this.feignClient = feignClient;
		this.kafkaProducer = kafkaProducer;
		this.chatFeginClient = chatFeginClient;
		this.groupRealtimeRepository = groupRealtimeRepository;
	}

	public boolean checkLeader(Long groupId, String memberUuid) {
		GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
			.findLatestGroupModifiableByGroupId(groupId);

		if (memberUuid.equals(groupModifiableEntity.getLeaderUuid())) {
			return true;
		} else {
			throw new NotGroupLeaderException("권한이 없습니다.");
		}
	}

	public String createGroup(CreateGroupDto groupDto) {
		GroupEntity groupEntity = GroupEntity.builder()
			.groupUuid(UUID.randomUUID().toString())
			.createdDatetime(LocalDateTime.now())
			.build();
		GroupEntity group = groupDetailRepository.save(groupEntity);

		GroupModifiableEntity groupModifiableEntity = GroupModifiableEntity.builder()
			.groupId(groupDetailRepository.findByGroupUuid(groupEntity.getGroupUuid()).getId())
			.categoryTbId(categoryRepository.findByCategoryName(groupDto.getCategory()).getId())
			.genderOptionsTbId(genderOptionsRepository.findByGenderDescription(groupDto.getGenderOptions()).getId())
			.title(groupDto.getTitle())
			.groupStartDatetime(groupDto.getGroupStartDatetime())
			.maxParticipant(groupDto.getMaxParticipant())
			.leaderUuid(groupDto.getLeaderUuid())
			.privateYn(groupDto.isPrivacyYn())
			.lat(groupDto.getLat())
			.lng(groupDto.getLng())
			.minAge(groupDto.getMinAge())
			.maxAge(groupDto.getMaxAge())
			.description(groupDto.getDescription())
			.latestYn(true)
			.createdDatetime(LocalDateTime.now())
			.locationName(groupDto.getLocationName())
			.locationAddress(groupDto.getLocationAddress())
			.build();
		groupModifiableRepository.save(groupModifiableEntity);

		GroupParticipantEntity leader = GroupParticipantEntity.builder()
			.groupId(groupEntity.getId())
			.memberUuid(groupDto.getLeaderUuid())
			.createdDatetime(LocalDateTime.now())
			.build();
		groupParticipantRepository.save(leader);

		groupDto.getTags()
			.forEach(hashtag -> {
				TagEntity tag = TagEntity.builder()
					.tagName(hashtag)
					.createdDatetime(LocalDateTime.now())
					.build();

				tagRepository.save(tag);

				GroupTagEntity groupTag = GroupTagEntity.builder()
					.tagTbId(tag.getId())
					.groupModifiableTbId(groupModifiableEntity.getGroupId())
					.createdDatetime(LocalDateTime.now())
					.build();

				groupTagRepository.save(groupTag);
			});

		GroupParticipantPublicStatusEntity groupParticipantPublicStatusEntity = GroupParticipantPublicStatusEntity.builder()
			.groupParticipantId(groupParticipantRepository
				.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), leader.getMemberUuid())
				.getId())
			.status(true)
			.createdDatetime(LocalDateTime.now())
			.build();



		groupParticipantPublicStatusRepository.save(groupParticipantPublicStatusEntity);

		ChatCommunicationDto chatCommunicationDto = new ChatCommunicationDto();
		chatCommunicationDto.setGroupUuid(groupEntity.getGroupUuid());
		chatCommunicationDto.setGroupName(groupDto.getTitle());
		chatCommunicationDto.setMemberUuid(groupDto.getLeaderUuid());
		chatCommunicationDto.setGroupType(groupDto.getCategory());
		chatCommunicationDto.setDeviceTokenList(feignClient.getMemberNotificationDetail(groupDto.getLeaderUuid()).getReceiverToken());

		chatFeginClient.create(chatCommunicationDto);

		return group.getGroupUuid();
	}

	public void delegateLeadership(DelegateDto delegateDto) {
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(delegateDto.getGroupUuid());

		GroupModifiableEntity latest = groupModifiableRepository
			.findLatestGroupModifiableByGroupId(groupEntity.getId());

		checkLeader(groupEntity.getId(), delegateDto.getLeaderUuid());

		latest.updateLatestYn(false);
		groupModifiableRepository.save(latest);

		GroupModifiableEntity groupModifiableEntity = GroupModifiableEntity.builder()
			.groupId(latest.getGroupId())
			.categoryTbId(latest.getCategoryTbId())
			.genderOptionsTbId(latest.getGenderOptionsTbId())
			.title(latest.getTitle())
			.groupStartDatetime(latest.getGroupStartDatetime())
			.maxParticipant(latest.getMaxParticipant())
			.leaderUuid(delegateDto.getDelegatedUuid())
			.privateYn(latest.getPrivateYn())
			.lat(latest.getLat())
			.lng(latest.getLng())
			.minAge(latest.getMinAge())
			.maxAge(latest.getMaxAge())
			.description(latest.getDescription())
			.latestYn(true)
			.createdDatetime(LocalDateTime.now())
			.locationName(latest.getLocationName())
			.locationAddress(latest.getLocationAddress())
			.build();

		groupModifiableRepository.save(groupModifiableEntity);

		List<GroupTagEntity> groupTagEntities = groupTagRepository.findByGroupModifiableTbId(latest.getId());
		groupTagEntities.forEach(groupTagEntity -> {

			GroupTagEntity newGroupTag = GroupTagEntity.builder()
				.tagTbId(groupTagEntity.getTagTbId())
				.groupModifiableTbId(latest.getGroupId())
				.createdDatetime(LocalDateTime.now())
				.build();
			groupTagRepository.save(newGroupTag);
		});

		MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(
			delegateDto.getDelegatedUuid());
		MemberNotificationResponseDto requestLeaderDto = feignClient.getMemberNotificationDetail(
			delegateDto.getLeaderUuid());
		MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
		memberNotificationDetailDto.setReceiverUuid(delegateDto.getDelegatedUuid());
		memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
		memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
		List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
		memberNotificationList.add(memberNotificationDetailDto);
		GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
		groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
		groupNotificationDetailDto.setGroupName(groupModifiableEntity.getTitle());
		GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
		groupNotificationDto.setTopic("group-delegate");
		groupNotificationDto.setType("group");
		groupNotificationDto.setSenderUuid(delegateDto.getLeaderUuid());
		groupNotificationDto.setSenderName(requestLeaderDto.getReceiverName());
		groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
		groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
		kafkaProducer.send("group-delegate", groupNotificationDto);
	}

	public static int calculateAge(LocalDateTime birthDate) {
		LocalDateTime currentDate = LocalDateTime.now();
		int age = currentDate.getYear() - birthDate.getYear();

		int month1 = currentDate.getMonthValue();
		int month2 = birthDate.getMonthValue();
		int day1 = currentDate.getDayOfMonth();
		int day2 = birthDate.getDayOfMonth();

		if (month1 < month2 || (month1 == month2 && day1 < day2)) {
			age--;
		}

		return age;
	}

	public GroupApplicantListDto getGroupApplicantList(GroupLeaderDto groupLeaderDto) {
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupLeaderDto.getGroupUuid());

		checkLeader(groupEntity.getId(), groupLeaderDto.getLeaderUuid());

		GroupApplicantListDto applicantList = new GroupApplicantListDto();

		List<GroupApplicantDto> applicants = groupApplicantRepository
			.findByGroupIdApplicantsWithNoStatus(groupEntity.getId())
			.stream()
			.map(groupApplicantEntity -> {
				GroupApplicantDto applicantDto = new GroupApplicantDto();
				applicantDto.setMemberUuid(groupApplicantEntity.getMemberUuid());
				// 유저랑 통신해서 프사랑 즐찾 가져와야됨
				MemberGroupDetailCommunicationDto memberDetailList = feignClient.getProfilePicture(groupApplicantEntity.getMemberUuid()).getMemberDetailList();
				
				applicantDto.setAge(calculateAge(memberDetailList.getBirthDate()));
				applicantDto.setMemberName(memberDetailList.getMemberName());
				applicantDto.setMemberGender(memberDetailList.getMemberGender());
				applicantDto.setProfilePicture(memberDetailList.getProfilePicture());

				return applicantDto;
			}).collect(Collectors.toList());
		applicantList.setApplicants(applicants);
		applicantList.setGroupUuid(groupLeaderDto.getGroupUuid());

		return applicantList;
	}

	public void modifyGroup(ModifyGroupDto modifyGroupDto) {
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(modifyGroupDto.getGroupUuid());

		checkLeader(groupEntity.getId(), modifyGroupDto.getLeaderUuid());

		GroupModifiableEntity latest = groupModifiableRepository
			.findLatestGroupModifiableByGroupId(groupEntity.getId());

		latest.updateLatestYn(false);
		groupModifiableRepository.save(latest);

		GroupModifiableEntity groupModifiableEntity = GroupModifiableEntity.builder()
			.groupId(groupEntity.getId())
			.categoryTbId(categoryRepository.findByCategoryName(modifyGroupDto.getCategory()).getId())
			.genderOptionsTbId(genderOptionsRepository.findByGenderDescription(modifyGroupDto.getGenderOptions()).getId())
			.title(modifyGroupDto.getTitle())
			.groupStartDatetime(modifyGroupDto.getGroupStartDatetime())
			.maxParticipant(modifyGroupDto.getMaxParticipant())
			.leaderUuid(modifyGroupDto.getLeaderUuid())
			.privateYn(modifyGroupDto.isPrivacyYn())
			.lat(modifyGroupDto.getLat())
			.lng(modifyGroupDto.getLng())
			.locationName(modifyGroupDto.getLocationName())
			.locationAddress(modifyGroupDto.getLocationAddress())
			.minAge(modifyGroupDto.getMinAge())
			.maxAge(modifyGroupDto.getMaxAge())
			.description(modifyGroupDto.getDescription())
			.createdDatetime(LocalDateTime.now())
			.latestYn(true)
			.build();

		groupModifiableRepository.save(groupModifiableEntity);

		modifyGroupDto.getTags()
			.forEach(hashtag -> {
				if (tagRepository.findByTagName(hashtag) == null) {

					TagEntity tag = TagEntity.builder()
						.tagName(hashtag)
						.createdDatetime(LocalDateTime.now())
						.build();

					tagRepository.save(tag);

					GroupTagEntity groupTag = GroupTagEntity.builder()
						.tagTbId(tag.getId())
						.groupModifiableTbId(groupModifiableEntity.getGroupId())
						.createdDatetime(LocalDateTime.now())
						.build();

					groupTagRepository.save(groupTag);
				} else {
					TagEntity tag = tagRepository.findByTagName(hashtag);

					GroupTagEntity groupTag = GroupTagEntity.builder()
						.tagTbId(tag.getId())
						.groupModifiableTbId(groupModifiableEntity.getGroupId())
						.createdDatetime(LocalDateTime.now())
						.build();

					groupTagRepository.save(groupTag);
				}
			});

		ChatCommunicationDto chatCommunicationDto = new ChatCommunicationDto();
		chatCommunicationDto.setGroupUuid(groupEntity.getGroupUuid());
		chatCommunicationDto.setGroupName(modifyGroupDto.getTitle());
		chatCommunicationDto.setMemberUuid("");
		chatCommunicationDto.setGroupType(modifyGroupDto.getCategory());

		chatFeginClient.modify(chatCommunicationDto);

		List<GroupParticipantEntity> participantList = groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(
			groupEntity.getId());

		List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();

		participantList.forEach(participant -> {
			MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(
				participant.getMemberUuid());

			MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
			memberNotificationDetailDto.setReceiverUuid(participant.getMemberUuid());
			memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
			memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());

			memberNotificationList.add(memberNotificationDetailDto);
		});

		GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();

		groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
		groupNotificationDetailDto.setGroupName(groupModifiableEntity.getTitle());

		MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(
			groupModifiableEntity.getLeaderUuid());

		GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
		groupNotificationDto.setTopic("group-update");
		groupNotificationDto.setType("group");
		groupNotificationDto.setSenderUuid(groupModifiableEntity.getLeaderUuid());
		groupNotificationDto.setSenderName(requestDto.getReceiverName());
		groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
		groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
		kafkaProducer.send("group-update", groupNotificationDto);
	}

	public void kickMember(KickDto kickDto) {
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(kickDto.getGroupUuid());

		if (checkLeader(groupEntity.getId(), kickDto.getLeaderUuid())) {
			GroupParticipantEntity kickedMember = groupParticipantRepository
				.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), kickDto.getKickUuid());

			GroupParticipantStatusEntity status = GroupParticipantStatusEntity.builder()
				.id(kickedMember.getId())
				.createdDatetime(LocalDateTime.now())
				.build();

			groupParticipantStatusRepository.save(status);

			MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(kickDto.getKickUuid());
			MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
			memberNotificationDetailDto.setReceiverUuid(kickDto.getKickUuid());
			memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
			memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
			List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
			memberNotificationList.add(memberNotificationDetailDto);
			GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
			groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
			groupNotificationDetailDto.setGroupName(groupModifiableRepository
				.findLatestGroupModifiableByGroupId(groupEntity.getId()).getTitle());
			GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
			groupNotificationDto.setTopic("group-kick");
			groupNotificationDto.setType("group");
			groupNotificationDto.setSenderUuid("");
			groupNotificationDto.setSenderName("");
			groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
			groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
			kafkaProducer.send("group-kick", groupNotificationDto);

			ChatCommunicationDto chatCommunicationDto = new ChatCommunicationDto();
			chatCommunicationDto.setGroupUuid(groupEntity.getGroupUuid());
			chatCommunicationDto.setMemberUuid(kickDto.getKickUuid());

			chatFeginClient.exit(chatCommunicationDto);
		}
	}

	public void confirmGroup(GroupLeaderDto groupLeaderDto) {
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupLeaderDto.getGroupUuid());

		checkLeader(groupEntity.getId(), groupLeaderDto.getLeaderUuid());

		GroupConfirmEntity confirmEntity = GroupConfirmEntity.builder()
			.createdDatetime(LocalDateTime.now())
			.id(groupEntity.getId())
			.build();

		groupConfirmRepository.save(confirmEntity);

		List<GroupParticipantEntity> participantList = groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(
			groupEntity.getId());

		List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();

		participantList.forEach(participant -> {
			MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(
				participant.getMemberUuid());

			MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
			memberNotificationDetailDto.setReceiverUuid(participant.getMemberUuid());
			memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
			memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());

			memberNotificationList.add(memberNotificationDetailDto);
		});

		GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();

		groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
		groupNotificationDetailDto.setGroupName(groupModifiableRepository
			.findLatestGroupModifiableByGroupId(groupEntity.getId()).getTitle());

		MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(
			groupLeaderDto.getLeaderUuid());

		GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
		groupNotificationDto.setTopic("group-confirm");
		groupNotificationDto.setType("group");
		groupNotificationDto.setSenderUuid(groupLeaderDto.getLeaderUuid());
		groupNotificationDto.setSenderName(requestDto.getReceiverName());
		groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
		groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
		kafkaProducer.send("group-confirm", groupNotificationDto);
	}

	public void alertLeaderToConfirm(LocalDateTime startTime, LocalDateTime endTime){
		List<GroupEntity> groupsNeedConfirm = groupDetailRepository.findGroupsNeedConfirm(startTime, endTime);

		groupsNeedConfirm
			.forEach(groupEntity -> {
				GroupModifiableEntity modifiable = groupModifiableRepository.findLatestGroupModifiableByGroupId(groupEntity.getId());

				MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(modifiable.getLeaderUuid());
				MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
				memberNotificationDetailDto.setReceiverUuid(modifiable.getLeaderUuid());
				memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
				memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
				List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
				memberNotificationList.add(memberNotificationDetailDto);
				GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
				groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
				groupNotificationDetailDto.setGroupName(modifiable.getTitle());
				GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
				groupNotificationDto.setTopic("group-remind-leader");
				groupNotificationDto.setType("group");
				groupNotificationDto.setSenderUuid("");
				groupNotificationDto.setSenderName("");
				groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
				groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
				kafkaProducer.send("group-remind-leader", groupNotificationDto);

				System.out.println("찍힘");
			});
	}

	public void alertParticipantRealTimeLocation(LocalDateTime startTime, LocalDateTime endTime){
		List<GroupEntity> groupsRealTimeLocation = groupDetailRepository.findGroupsRealTimeLocation(startTime, endTime);

		groupsRealTimeLocation.forEach(groupEntity -> {
			List<GroupParticipantEntity> participantList = groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(
				groupEntity.getId());

			GroupRealtimeEntity groupRealtime = GroupRealtimeEntity.builder()
					.id(groupEntity.getId())
					.createdDatetime(LocalDateTime.now())
					.build();

			groupRealtimeRepository.save(groupRealtime);

			List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();

			participantList.forEach(participant -> {
				MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(
					participant.getMemberUuid());

				MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
				memberNotificationDetailDto.setReceiverUuid(participant.getMemberUuid());
				memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
				memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());

				memberNotificationList.add(memberNotificationDetailDto);
			});

			GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();

			groupNotificationDetailDto.setGroupUuid(groupEntity.getGroupUuid());
			groupNotificationDetailDto.setGroupName(groupModifiableRepository
				.findLatestGroupModifiableByGroupId(groupEntity.getId()).getTitle());


			GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
			groupNotificationDto.setTopic("group-remind");
			groupNotificationDto.setType("group");
			groupNotificationDto.setSenderUuid("");
			groupNotificationDto.setSenderName("");
			groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
			groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
			kafkaProducer.send("group-remind", groupNotificationDto);
		});
	}

	public void acceptApplicant(ApplicantAcceptRejectDto applicantAcceptRejectDto) {
		GroupApplicantEntity applicant = groupApplicantRepository
			.findByGroupIdAndMemberUuidApplicantsWithNoStatus(groupDetailRepository
					.findByGroupUuid(applicantAcceptRejectDto.getGroupUuid()).getId(),
				applicantAcceptRejectDto.getMemberUuid());

		GroupApplicantStatusEntity status = GroupApplicantStatusEntity.builder()
			.id(applicant.getId())
			.applicantStatus(true)
			.createdDatetime(LocalDateTime.now())
			.build();

		groupApplicantStatusRepository.save(status);

		GroupParticipantEntity participant = GroupParticipantEntity.builder()
			.groupId(applicant.getGroupId())
			.memberUuid(applicant.getMemberUuid())
			.createdDatetime(LocalDateTime.now())
			.build();

		groupParticipantRepository.save(participant);

		GroupParticipantPublicStatusEntity groupParticipantPublicStatusEntity = GroupParticipantPublicStatusEntity.builder()
				.groupParticipantId(groupParticipantRepository
						.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(applicant.getGroupId(), applicantAcceptRejectDto.getMemberUuid())
						.getId())
				.status(true)
				.createdDatetime(LocalDateTime.now())
				.build();

		groupParticipantPublicStatusRepository.save(groupParticipantPublicStatusEntity);

		MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(participant.getMemberUuid());
		MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
		memberNotificationDetailDto.setReceiverUuid(applicant.getMemberUuid());
		memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
		memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
		List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
		memberNotificationList.add(memberNotificationDetailDto);
		GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
		GroupEntity group = groupDetailRepository
			.findByGroupUuid(applicantAcceptRejectDto.getGroupUuid());
		groupNotificationDetailDto.setGroupUuid(group.getGroupUuid());
		groupNotificationDetailDto.setGroupName(groupModifiableRepository
			.findLatestGroupModifiableByGroupId(group.getId()).getTitle());
		GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
		groupNotificationDto.setTopic("group-affirm");
		groupNotificationDto.setType("group");
		groupNotificationDto.setSenderUuid("");
		groupNotificationDto.setSenderName("");
		groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
		groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
		kafkaProducer.send("group-affirm", groupNotificationDto);

		ChatCommunicationDto chatCommunicationDto = new ChatCommunicationDto();
		chatCommunicationDto.setGroupUuid(applicantAcceptRejectDto.getGroupUuid());
		chatCommunicationDto.setMemberUuid(applicantAcceptRejectDto.getMemberUuid());
		chatCommunicationDto.setDeviceTokenList(requestDto.getReceiverToken());

		chatFeginClient.accept(chatCommunicationDto);
	}

	public void rejectApplicant(ApplicantAcceptRejectDto applicantAcceptRejectDto) {
		GroupApplicantEntity applicant = groupApplicantRepository
			.findByGroupIdAndMemberUuidApplicantsWithNoStatus(groupDetailRepository
					.findByGroupUuid(applicantAcceptRejectDto.getGroupUuid()).getId(),
				applicantAcceptRejectDto.getMemberUuid());

		GroupApplicantStatusEntity status = GroupApplicantStatusEntity.builder()
			.id(applicant.getId())
			.applicantStatus(false)
			.createdDatetime(LocalDateTime.now())
			.build();

		groupApplicantStatusRepository.save(status);

		MemberNotificationResponseDto requestDto = feignClient.getMemberNotificationDetail(applicant.getMemberUuid());
		MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
		memberNotificationDetailDto.setReceiverUuid(applicant.getMemberUuid());
		memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
		memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());
		List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();
		memberNotificationList.add(memberNotificationDetailDto);
		GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
		GroupEntity group = groupDetailRepository
			.findByGroupUuid(applicantAcceptRejectDto.getGroupUuid());
		groupNotificationDetailDto.setGroupUuid(group.getGroupUuid());
		groupNotificationDetailDto.setGroupName(groupModifiableRepository
			.findLatestGroupModifiableByGroupId(group.getId()).getTitle());
		GroupNotificationDto groupNotificationDto = new GroupNotificationDto();
		groupNotificationDto.setTopic("group-reject");
		groupNotificationDto.setType("group");
		groupNotificationDto.setSenderUuid("");
		groupNotificationDto.setSenderName("");
		groupNotificationDto.setGroupNotificationDetailDto(groupNotificationDetailDto);
		groupNotificationDto.setMemberNotificationDetailDtoList(memberNotificationList);
		kafkaProducer.send("group-reject", groupNotificationDto);
	}
}
