package com.mohey.groupservice.interprocess.client;

import java.util.List;

import com.mohey.groupservice.interprocess.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@org.springframework.cloud.openfeign.FeignClient(name = "member-service",url = "http://127.0.0.1:8098/members")
public interface FeignClient {
	@GetMapping("/device/getToken/{memberUuid}")
	MemberNotificationResponseDto getMemberNotificationDetail(@PathVariable String memberUuid);

	@PostMapping("/members/info/getParticipantsDetail")
	MemberDetailResponseDto getProfilePicture(@RequestBody String participantsUuid);

	@GetMapping("/fein/{memberUuid}")
	MemberFriendListResponseDto getFriendsList(@PathVariable String memberUuid);

	@GetMapping("/{memberUuid}/getUsername")
	MemberNameResponseDto getMemberName(@PathVariable String memberUuid);

	@GetMapping("/friendSearch/{memberUuid}")
	MemberFriendDetailListResponseDto getFriendsDetailList(@PathVariable String memberUuid);

	@GetMapping("/friendSearch/{memberUuId}/{friendNickname}")
	MemberFriendDetailListResponseDto getFriendsDetailListBySearch(@PathVariable("memberUuId") String memberUuId,
																   @PathVariable("friendNickname") String nickname);

}
