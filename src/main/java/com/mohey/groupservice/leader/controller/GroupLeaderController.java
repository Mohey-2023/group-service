package com.mohey.groupservice.leader.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mohey.groupservice.detail.dto.GroupDto;
import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;
import com.mohey.groupservice.leader.dto.leader.CreateGroupDto;
import com.mohey.groupservice.leader.dto.leader.DelegateDto;
import com.mohey.groupservice.leader.dto.leader.GroupLeaderDto;
import com.mohey.groupservice.leader.dto.leader.KickDto;
import com.mohey.groupservice.leader.dto.leader.ModifyGroupDto;
import com.mohey.groupservice.leader.service.GroupLeaderService;

@RestController
@RequestMapping("/groups")
public class GroupLeaderController {

	private final GroupLeaderService groupLeaderService;

	@Autowired
	public GroupLeaderController(GroupLeaderService groupLeaderService) {
		this.groupLeaderService = groupLeaderService;
	}

	@PostMapping("/create")
	public ResponseEntity<Void> createGroup(@RequestBody CreateGroupDto groupDto) {
		groupLeaderService.createGroup(groupDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/delegate")
	public ResponseEntity<Void> delegateLeadership(@RequestBody DelegateDto delegateDto) {
		groupLeaderService.delegateLeadership(delegateDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/applicants")
	public ResponseEntity<List<GroupApplicantEntity>> getGroupApplicants(@RequestBody GroupLeaderDto groupLeaderDto) {
		List<GroupApplicantEntity> groupApplicants = groupLeaderService.getGroupApplicants(groupLeaderDto);
		if (groupApplicants != null) {
			return new ResponseEntity<>(groupApplicants, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/modify")
	public ResponseEntity<Void> modifyGroup(@RequestBody ModifyGroupDto modifyGroupDto) {
		groupLeaderService.modifyGroup(modifyGroupDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/kick")
	public ResponseEntity<Void> kickMember(@RequestBody KickDto kickDto) {
		groupLeaderService.kickMember(kickDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/confirm")
	public ResponseEntity<Void> confirmGroup(@RequestBody String groupUuid) {
		groupLeaderService.confirmGroup(groupUuid);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}