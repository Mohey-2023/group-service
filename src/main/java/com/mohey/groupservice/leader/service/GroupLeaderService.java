package com.mohey.groupservice.leader.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;
import com.mohey.groupservice.entity.applicant.GroupApplicantStatusEntity;
import com.mohey.groupservice.entity.group.GroupConfirmEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
import com.mohey.groupservice.leader.dto.applicant.ApplicantAcceptRejectDto;
import com.mohey.groupservice.leader.dto.applicant.GroupApplicantDto;
import com.mohey.groupservice.leader.dto.applicant.GroupApplicantListDto;
import com.mohey.groupservice.leader.dto.leader.DelegateDto;
import com.mohey.groupservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.entity.category.CategoryEntity;
import com.mohey.groupservice.entity.group.GenderOptionsEntity;
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
	private final GroupParticipantRepository groupParticipantRepository;
	private final CategoryRepository categoryRepository;
	private final GenderOptionsRepository genderOptionsRepository;
	private final GroupApplicantRepository groupApplicantRepository;
	private final GroupApplicantStatusRepository groupApplicantStatusRepository;
	private final GroupParticipantStatusRepository groupParticipantStatusRepository;
	private final GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository;

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
							  GroupConfirmRepository groupConfirmRepository
		){
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
	}

	public boolean checkLeader(Long groupId, String memberUuid){
		GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
			.findLatestGroupModifiableByGroupId(groupId);

		if(memberUuid.equals(groupModifiableEntity.getLeaderUuid())){
			return true;
		}
		return false;
	}

	public void createGroup(CreateGroupDto groupDto){
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

		GroupParticipantPublicStatusEntity groupParticipantPublicStatusEntity = GroupParticipantPublicStatusEntity.builder()
				.groupParticipantId(groupParticipantRepository
						.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), leader.getMemberUuid()).getId())
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

		GroupModifiableEntity latest = groupModifiableRepository
				.findLatestGroupModifiableByGroupId(groupEntity.getId());

		if (modifyGroupDto.getLeaderUuid().equals(latest.getLeaderUuid())) {
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

			// chats한테 groupuuid, groupname, category 보내기
		}
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
		}
	}

	public void confirmGroup(String groupUuid){
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupUuid);

		GroupConfirmEntity confirmEntity = GroupConfirmEntity.builder()
				.createdDatetime(LocalDateTime.now())
				.id(groupEntity.getId())
				.build();

		groupConfirmRepository.save(confirmEntity);
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

		// chat한테 groupuuid, memberuuid
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
	}
}
