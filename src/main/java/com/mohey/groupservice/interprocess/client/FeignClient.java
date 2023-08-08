package com.mohey.groupservice.interprocess.client;

import java.util.List;

import javax.ws.rs.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mohey.groupservice.interprocess.dto.MemberFriendListResponseDto;
import com.mohey.groupservice.interprocess.dto.MemberGroupDetailCommunicationDto;
import com.mohey.groupservice.interprocess.dto.MemberNameResponseDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationResponseDto;

@org.springframework.cloud.openfeign.FeignClient(name = "member-service",url = "http://127.0.0.1:8098/members")
public interface FeignClient {
	@GetMapping("/device/getToken/{memberUuid}")
	MemberNotificationResponseDto getMemberNotificationDetail(@PathVariable String memberUuid);

	@PostMapping("/members/info/getParticipantsDetail")
	MemberGroupDetailCommunicationDto getProfilePicture(@RequestBody List<String> participantsUuid);

	@GetMapping("/fein/{memberUuid}")
	MemberFriendListResponseDto getFriendsList(@PathVariable String memberUuid);

	@GetMapping("/{memberUuid}/getUsername")
	MemberNameResponseDto getMemberName(@PathVariable String memberUuid);
}
