package com.mohey.groupservice.detail.controller;

import com.mohey.groupservice.detail.dto.*;
import com.mohey.groupservice.detail.service.GroupDetailService;
import com.mohey.groupservice.interprocess.dto.MemberFriendDetailListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupDetailController {
    private final GroupDetailService groupDetailService;

    @Autowired
    public GroupDetailController(GroupDetailService groupDetailService) {
        this.groupDetailService = groupDetailService;
    }

    @GetMapping("/{groupUuid}")
    public ResponseEntity<GroupDto> getGroupDetail(@PathVariable String groupUuid) {
        GroupDto group = groupDetailService.getGroupDetailByGroupId(groupUuid);
        if (group != null) {
            return new ResponseEntity<>(group, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/participants")
    public ResponseEntity<GroupParticipantListDto> getGroupParticipants(@RequestBody GroupParticipantRequestDto groupParticipantRequestDto) {
        return new ResponseEntity<>(groupDetailService.getGroupParticipantList(groupParticipantRequestDto), HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteGroup(@RequestBody String groupUuid) {
        groupDetailService.deleteGroup(groupUuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/public")
    public ResponseEntity<Void> setGroupPublicStatus(@RequestBody PublicStatusDto publicStatus) {
        groupDetailService.setGroupPublicStatus(publicStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/friendList/{memberUuid}")
    public ResponseEntity<List<MemberFriendDetailListDto>> getFriendList(@PathVariable String memberUuid){
        return new ResponseEntity<>(groupDetailService.getFriendsList(memberUuid), HttpStatus.OK);
    }

    @GetMapping("/friendList/{memberUuid}/{keyword}")
    public ResponseEntity<List<MemberFriendDetailListDto>> getFriendListBySearch(@PathVariable String memberUuid,
                                                                                 @PathVariable String keyword){
        return new ResponseEntity<>(groupDetailService.getFriendsListBySearch(memberUuid, keyword), HttpStatus.OK);
    }

    @PostMapping("/friendList/invite")
    public ResponseEntity<Void> inviteFriend(@RequestBody GroupInviteDto groupInviteDto){
        groupDetailService.inviteFriend(groupInviteDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}