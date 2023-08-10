package com.mohey.groupservice.list.controller;

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

import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.list.dto.CalendarRequestDto;
import com.mohey.groupservice.list.dto.CalendarResponseDto;
import com.mohey.groupservice.list.dto.FriendGroupDto;
import com.mohey.groupservice.list.dto.MapGroupListRequestDto;
import com.mohey.groupservice.list.dto.MapGroupListResponseDto;
import com.mohey.groupservice.list.dto.MyGroupListMainPageDto;
import com.mohey.groupservice.list.dto.MyFutureGroupListMyPageDto;
import com.mohey.groupservice.list.dto.MyPastGroupListMyPageDto;
import com.mohey.groupservice.list.dto.YourGroupListDto;
import com.mohey.groupservice.list.service.GroupListService;

@RestController
@RequestMapping("/groups")
public class GroupListController {

	private final GroupListService groupListService;

	@Autowired
	public GroupListController(GroupListService groupListService) {
		this.groupListService = groupListService;
	}

	@PostMapping(value = "/calendar", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<CalendarResponseDto>> getCalendarGroupList(@RequestBody CalendarRequestDto calendarRequestDto) {
		List<CalendarResponseDto> calendarGroupList = groupListService.getCalendarGroupList(calendarRequestDto);
		return new ResponseEntity<>(calendarGroupList, HttpStatus.OK);
	}

	@GetMapping(value = "/main-page/{memberUuid}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<MyGroupListMainPageDto>> getMyMainPageGroupList(@PathVariable String memberUuid) {
		List<MyGroupListMainPageDto> myMainPageGroupList = groupListService.getMyMainPageGroupList(memberUuid);
		return new ResponseEntity<>(myMainPageGroupList, HttpStatus.OK);
	}

	@GetMapping(value = "/my-page/{memberUuid}/upcoming", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<MyFutureGroupListMyPageDto>> getMyPageUpcomingGroupList(@PathVariable String memberUuid) {
		List<MyFutureGroupListMyPageDto> myPageGroupList = groupListService.getMyPageUpcomingGroupList(memberUuid);
		return new ResponseEntity<>(myPageGroupList, HttpStatus.OK);
	}

	@GetMapping(value = "/my-page/{memberUuid}/completed", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<MyPastGroupListMyPageDto>> getMyPageCompletedGroupList(@PathVariable String memberUuid){
		List<MyPastGroupListMyPageDto> myPageGroupList = groupListService.getMyPageCompletedGroupList(memberUuid);
		return new ResponseEntity<>(myPageGroupList, HttpStatus.OK);
	}

	@GetMapping(value = "/your-page/{memberUuid}/upcoming", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<YourGroupListDto>> getYourPageUpcomingGroupList(@PathVariable String memberUuid) {
		List<YourGroupListDto> yourPageGroupList = groupListService.getYourPageUpcomingConfirmedGroupList(memberUuid);

		return new ResponseEntity<>(yourPageGroupList, HttpStatus.OK);
	}

	@GetMapping(value = "/your-page/{memberUuid}/completed", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<YourGroupListDto>> getYourPageCompletedGroupList(@PathVariable String memberUuid) {
		List<YourGroupListDto> yourPageGroupList = groupListService.getYourPageCompletedGroupList(memberUuid);

		return new ResponseEntity<>(yourPageGroupList, HttpStatus.OK);
	}

	@PostMapping(value = "/map", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<MapGroupListResponseDto>> getMapGroupList(@RequestBody MapGroupListRequestDto mapGroupListRequestDto) {
		List<MapGroupListResponseDto> mapGroupList = groupListService.getMapGroupList(mapGroupListRequestDto);
		return new ResponseEntity<>(mapGroupList, HttpStatus.OK);
	}

	@GetMapping(value = "/friend-group/{memberUuid}", produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<FriendGroupDto>> getFriendGroupList(@PathVariable String memberUuid){
		return new ResponseEntity<>(groupListService.getFriendsGroupList(memberUuid), HttpStatus.OK);
	}
}