package com.mohey.groupservice.interprocess.client;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mohey.groupservice.interprocess.dto.MemberGroupDetailCommunicationDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationRequestDto;

@org.springframework.cloud.openfeign.FeignClient(name = "member-service",url = "http://127.0.0.1:8098/members")
public interface FeignClient {
	@GetMapping("/device/getToken/{memberUuid}")
	MemberNotificationRequestDto getMemberNotificationDetail(@PathVariable String memberUuid);

	@PostMapping("/members/info/getParticipantsDetail")
	List<MemberGroupDetailCommunicationDto> getProfilePictureAndFavorite(@RequestBody List<String> participantsUuid);

	@GetMapping()
	List<String> getFriendsList(@RequestParam String memberUuid);

}
