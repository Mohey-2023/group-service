package com.mohey.groupservice.leader.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;
import com.mohey.groupservice.entity.group.GroupConfirmEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantPublicStatusEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantStatusEntity;
import com.mohey.groupservice.leader.dto.leader.DelegateDto;
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
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupApplicantRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantPublicStatusRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;
@Service
public class GroupLeaderService {
	private final GroupDetailRepository groupDetailRepository;
	private final GroupModifiableRepository groupModifiableRepository;
	private final GroupTagRepository groupTagRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final CategoryRepository categoryRepository;
	private final GenderOptionsRepository genderOptionsRepository;
	private final GroupApplicantRepository groupApplicantRepository;
	private final GroupParticipantPublicStatusRepository groupParticipantPublicStatusRepository;

	@Autowired
	public GroupLeaderService(GroupDetailRepository groupDetailRepository,
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

	public boolean checkLeader(Long groupId, String memberUuid){
		GroupModifiableEntity groupModifiableEntity = groupModifiableRepository
			.findLatestGroupModifiableByGroupId(groupId);

		if(memberUuid != groupModifiableEntity.getLeaderUuid()){
			return false;
		}
		return true;
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
		groupModifiableEntity.setCreatedDatetime(LocalDateTime.now());
		groupModifiableEntity.setLocationId(groupDto.getLocationId());
		groupModifiableRepository.save(groupModifiableEntity);

		GroupParticipantEntity leader = new GroupParticipantEntity();
		leader.setGroupId(groupEntity.getId());
		leader.setMemberUuid(groupDto.getLeaderUuid());
		leader.setCreatedDatetime(LocalDateTime.now());
		groupParticipantRepository.save(leader);

		GroupParticipantPublicStatusEntity groupParticipantPublicStatusEntity = new GroupParticipantPublicStatusEntity();
		groupParticipantPublicStatusEntity.setGroupParticipantTbId(groupParticipantRepository
			.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), leader.getMemberUuid()).getId());
		groupParticipantPublicStatusEntity.setStatus(true);
		groupParticipantPublicStatusEntity.setCreatedDatetime(LocalDateTime.now());
		groupParticipantPublicStatusRepository.save(groupParticipantPublicStatusEntity);

	}

	public void delegateLeadership(DelegateDto delegateDto){
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(delegateDto.getGroupUuid());

		GroupModifiableEntity latest = groupModifiableRepository
				.findLatestGroupModifiableByGroupId(groupEntity.getId());
		latest.setLatestYn(false);
		groupModifiableRepository.save(latest);

		GroupModifiableEntity groupModifiableEntity = new GroupModifiableEntity();
		groupModifiableEntity = latest;

		groupModifiableEntity.setLeaderUuid(delegateDto.getDelegatedUuid());
		groupModifiableEntity.setLatestYn(true);
		groupModifiableEntity.setCreatedDatetime(null);

		groupModifiableRepository.save(groupModifiableEntity);
	}

	public List<GroupApplicantEntity> getGroupApplicants(GroupLeaderDto groupLeaderDto) {
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupLeaderDto.getGroupUuid());

		if(!checkLeader(groupEntity.getId(), groupLeaderDto.getLeaderUuid())){
			return null;
		}

		if (groupEntity != null) {
			return groupApplicantRepository.findByGroupIdApplicantsWithNoStatus(groupEntity.getId());
		}
		return null;
	}


	public void modifyGroup(ModifyGroupDto modifyGroupDto){
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(modifyGroupDto.getGroupUuid());

		GroupModifiableEntity latest = groupModifiableRepository
			.findLatestGroupModifiableByGroupId(groupEntity.getId());

		GroupModifiableEntity groupModifiableEntity = new GroupModifiableEntity();

		if(modifyGroupDto.getLeaderUuid() == latest.getLeaderUuid()){
			groupModifiableEntity.setGroupTbId(groupEntity.getId());
			groupModifiableEntity.setCategoryTbId(categoryRepository
				.findByCategoryUuid(modifyGroupDto.getCategoryUuid())
				.getId());
			groupModifiableEntity.setGenderOptionsTbId(genderOptionsRepository
				.findByGenderUuid(modifyGroupDto.getGenderOptionsUuid())
				.getId());
			groupModifiableEntity.setTitle(modifyGroupDto.getTitle());
			groupModifiableEntity.setGroupStartDatetime(modifyGroupDto.getGroupStartDatetime());
			groupModifiableEntity.setMaxParticipant(modifyGroupDto.getMaxParticipant());
			groupModifiableEntity.setLeaderUuid(modifyGroupDto.getLeaderUuid());
			groupModifiableEntity.setPrivateYn(true);
			latest.setPrivateYn(false);
			groupModifiableRepository.save(latest);
			groupModifiableEntity.setLat(modifyGroupDto.getLat());
			groupModifiableEntity.setLng(modifyGroupDto.getLng());
			groupModifiableEntity.setMinAge(modifyGroupDto.getMinAge());
			groupModifiableEntity.setMaxAge(modifyGroupDto.getMaxAge());
			groupModifiableEntity.setPrivateYn(modifyGroupDto.isPrivacyYn());
			groupModifiableEntity.setDescription(modifyGroupDto.getDescription());
			groupModifiableRepository.save(groupModifiableEntity);
		}
	}

	public void kickMember(KickDto kickDto){
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(kickDto.getGroupUuid());

		if(checkLeader(groupEntity.getId(), kickDto.getLeaderUuid())){
			GroupParticipantEntity kickedMember = groupParticipantRepository
				.findByGroupIdAndMemberUuidAndGroupParticipantStatusIsNull(groupEntity.getId(), kickDto.getKickUuid());

			GroupParticipantStatusEntity status = new GroupParticipantStatusEntity();
			status.setId(groupEntity.getId());
			status.setCreatedDatetime(LocalDateTime.now());
			kickedMember.setGroupParticipantStatusEntity(status);
			groupParticipantRepository.save(kickedMember);
		}
	}

	public void confirmGroup(String groupUuid){
		GroupEntity groupEntity = groupDetailRepository.findByGroupUuid(groupUuid);

		GroupConfirmEntity confirmEntity = new GroupConfirmEntity();
		confirmEntity.setCreatedDatetime(LocalDateTime.now());
		confirmEntity.setId(groupEntity.getId());

		groupEntity.setGroupConfirm(confirmEntity);
		groupDetailRepository.save(groupEntity);
	}
}
