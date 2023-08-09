package com.mohey.groupservice.list.controller;

import java.util.List;

import javax.ws.rs.Path;

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
import com.mohey.groupservice.list.dto.MyGroupListMyPageDto;
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

	@GetMapping("/list/{memberUuid}")
	public ResponseEntity<List<GroupEntity>> getMemberGroupList(@PathVariable String memberUuid) {
		List<GroupEntity> memberGroupList = groupListService.getMemberGroupList(memberUuid);
		return new ResponseEntity<>(memberGroupList, HttpStatus.OK);
	}

	@PostMapping("/calendar")
	public ResponseEntity<List<CalendarResponseDto>> getCalendarGroupList(@RequestBody CalendarRequestDto calendarRequestDto) {
		List<CalendarResponseDto> calendarGroupList = groupListService.getCalendarGroupList(calendarRequestDto);
		return new ResponseEntity<>(calendarGroupList, HttpStatus.OK);
	}

	@GetMapping("/main-page/{memberUuid}")
	public ResponseEntity<List<MyGroupListMainPageDto>> getMyMainPageGroupList(@PathVariable String memberUuid) {
		List<MyGroupListMainPageDto> myMainPageGroupList = groupListService.getMyMainPageGroupList(memberUuid);
		return new ResponseEntity<>(myMainPageGroupList, HttpStatus.OK);
	}

	@GetMapping("/my-page/{memberUuid}")
	public ResponseEntity<List<MyGroupListMyPageDto>> getMyPageGroupList(@PathVariable String memberUuid) {
		List<MyGroupListMyPageDto> myPageGroupList = groupListService.getMyPageGroupList(memberUuid);
		return new ResponseEntity<>(myPageGroupList, HttpStatus.OK);
	}

	@GetMapping("/your-page/{memberUuid}")
	public ResponseEntity<List<YourGroupListDto>> getYourPageGroupList(@PathVariable String memberUuid) {
		List<YourGroupListDto> yourPageGroupList = groupListService.getYourPageGroupList(memberUuid);

		return new ResponseEntity<>(yourPageGroupList, HttpStatus.OK);
	}

	@PostMapping("/map")
	public ResponseEntity<List<MapGroupListResponseDto>> getMapGroupList(@RequestBody MapGroupListRequestDto mapGroupListRequestDto) {
		List<MapGroupListResponseDto> mapGroupList = groupListService.getMapGroupList(mapGroupListRequestDto);
		return new ResponseEntity<>(mapGroupList, HttpStatus.OK);
	}

	@GetMapping("/friend-group/{memberUuid}")
	public ResponseEntity<List<FriendGroupDto>> getFriendGroupList(@PathVariable String memberUuid){
		return new ResponseEntity<>(groupListService.getFriendsGroupList(memberUuid), HttpStatus.OK);
	}
}