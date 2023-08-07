package com.mohey.groupservice.interprocess.client;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mohey.groupservice.interprocess.dto.MemberGroupDetailCommunicationDto;
import com.mohey.groupservice.interprocess.dto.MemberRequestDto;
import com.mohey.groupservice.interprocess.dto.MemberNotificationDetailDto;

@org.springframework.cloud.openfeign.FeignClient(name = "member-service")
public interface FeignClient {

	@PostMapping()
	MemberNotificationDetailDto getMemberNotificationDetail(@RequestBody String memberUuid);

	@PostMapping()
	MemberGroupDetailCommunicationDto getProfilePictureAndFavorite(@RequestBody MemberRequestDto memberGroupDetailRequestDto);

	@PostMapping()
	List<String> getFriendsList(@RequestBody String memberUuid);

}
