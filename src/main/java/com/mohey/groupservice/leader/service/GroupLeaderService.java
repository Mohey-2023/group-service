package com.mohey.groupservice.leader.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;
import com.mohey.groupservice.entity.applicant.GroupApplicantStatusEntity;
import com.mohey.groupservice.entity.category.TagEntity;
import com.mohey.groupservice.entity.group.GroupConfirmEntity;
import com.mohey.groupservice.entity.group.GroupTagEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
import com.mohey.groupservice.exception.NotGroupLeaderException;
import com.mohey.groupservice.interprocess.client.FeignClient;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDetailDto;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationDetailDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationRequestDto;
import com.mohey.groupservice.kafka.KafkaProducer;
import com.mohey.groupservice.leader.dto.applicant.ApplicantAcceptRejectDto;
import com.mohey.groupservice.leader.dto.applicant.GroupApplicantDto;
import com.mohey.groupservice.leader.dto.applicant.GroupApplicantListDto;
import com.mohey.groupservice.leader.dto.leader.DelegateDto;
import com.mohey.groupservice.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
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
		KafkaProducer kafkaProducer
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

	public void createGroup(CreateGroupDto groupDto) {
		GroupEntity groupEntity = GroupEntity.builder()
			.groupUuid(UUID.randomUUID().toString())
			.createdDatetime(LocalDateTime.now())
			.build();
		groupDetailRepository.save(groupEntity);

		GroupModifiableEntity groupModifiableEntity = GroupModifiableEntity.builder()
			.groupId(groupDetailRepository.findByGroupUuid(groupEntity.getGroupUuid()).getId())
			.categoryTbId(categoryRepository.findByCategoryUuid(groupDto.getCategoryUuid()).getId())
			.genderOptionsTbId(genderOptionsRepository.findByGenderUuid(groupDto.getGenderOptionsUuid()).getId())
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

		// chats한테 groupuuid, gruopname, category, memberuuid 보내기
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

		MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(
			delegateDto.getDelegatedUuid());
		MemberNotificationRequestDto requestLeaderDto = feignClient.getMemberNotificationDetail(
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
			.categoryTbId(categoryRepository.findByCategoryUuid(modifyGroupDto.getCategory()).getId())
			.genderOptionsTbId(genderOptionsRepository.findByGenderUuid(modifyGroupDto.getGenderOptionsUuid()).getId())
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

		// chats한테 groupuuid, groupname, category 보내기
		List<GroupParticipantEntity> participantList = groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(
			groupEntity.getId());

		List<MemberNotificationDetailDto> memberNotificationList = new ArrayList<>();

		participantList.forEach(participant -> {
			MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(
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

		MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(
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
			// groupuuid, memberuuid

			MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(kickDto.getKickUuid());
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
			MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(
				participant.getMemberUuid());

			MemberNotificationDetailDto memberNotificationDetailDto = new MemberNotificationDetailDto();
			memberNotificationDetailDto.setReceiverUuid(participant.getMemberUuid());
			memberNotificationDetailDto.setReceiverName(requestDto.getReceiverName());
			memberNotificationDetailDto.setDeviceTokenList(requestDto.getReceiverToken());

			memberNotificationList.add(memberNotificationDetailDto);
		});

		GroupNotificationDetailDto groupNotificationDetailDto = new GroupNotificationDetailDto();
		GroupEntity group = groupDetailRepository
			.findByGroupUuid(groupLeaderDto.getGroupUuid());

		groupNotificationDetailDto.setGroupUuid(group.getGroupUuid());
		groupNotificationDetailDto.setGroupName(groupModifiableRepository
			.findLatestGroupModifiableByGroupId(group.getId()).getTitle());

		MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(
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

		MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(participant.getMemberUuid());
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

		MemberNotificationRequestDto requestDto = feignClient.getMemberNotificationDetail(applicant.getMemberUuid());
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
