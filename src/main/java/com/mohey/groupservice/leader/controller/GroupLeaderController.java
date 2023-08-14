package com.mohey.groupservice.leader.controller;

import java.util.List;

import com.mohey.groupservice.leader.dto.applicant.ApplicantAcceptRejectDto;
import com.mohey.groupservice.leader.dto.applicant.GroupApplicantListDto;
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
@RequestMapping(value = "/groups/leader")
public class GroupLeaderController {

	private final GroupLeaderService groupLeaderService;

	@Autowired
	public GroupLeaderController(GroupLeaderService groupLeaderService) {
		this.groupLeaderService = groupLeaderService;
	}

	@PostMapping("/create")
	public ResponseEntity<String> createGroup(@RequestBody CreateGroupDto groupDto) {
		return new ResponseEntity<>(groupLeaderService.createGroup(groupDto), HttpStatus.CREATED);
	}

	@PostMapping("/delegate")
	public ResponseEntity<Void> delegateLeadership(@RequestBody DelegateDto delegateDto) {
		groupLeaderService.delegateLeadership(delegateDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/applicants", produces = "application/json;charset=UTF-8")
	public ResponseEntity<GroupApplicantListDto> getGroupApplicants(@RequestBody GroupLeaderDto groupLeaderDto) {
		return new ResponseEntity<>(groupLeaderService.getGroupApplicantList(groupLeaderDto), HttpStatus.OK);
	}

	@PostMapping("/accept")
	public ResponseEntity<Void> acceptGroupApplicants(@RequestBody ApplicantAcceptRejectDto applicantAcceptRejectDto) {
		groupLeaderService.acceptApplicant(applicantAcceptRejectDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/reject")
	public ResponseEntity<Void> rejectGroupApplicants(@RequestBody ApplicantAcceptRejectDto applicantAcceptRejectDto) {
		groupLeaderService.rejectApplicant(applicantAcceptRejectDto);
		return new ResponseEntity<>(HttpStatus.OK);
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
	public ResponseEntity<Void> confirmGroup(@RequestBody GroupLeaderDto groupLeaderDto) {
		groupLeaderService.confirmGroup(groupLeaderDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}