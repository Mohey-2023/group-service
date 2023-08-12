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

    @GetMapping(value = "/{groupUuid}/{memberUuid}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<GroupDto> getGroupDetail(@PathVariable String groupUuid, @PathVariable String memberUuid) {
        GroupDto group = groupDetailService.getGroupDetailByGroupId(groupUuid, memberUuid);
        if (group != null) {
            return new ResponseEntity<>(group, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/invite/{groupUuid}/{memberUuid}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<String>> getInvited(@PathVariable String groupUuid, @PathVariable String memberUuid){
        return new ResponseEntity<>(groupDetailService.getInvitedHistory(groupUuid, memberUuid), HttpStatus.OK);
    }

    @GetMapping(value = "/participants/{groupUuid}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<GroupParticipantListDto> getGroupParticipants(@PathVariable String groupUuid) {
        return new ResponseEntity<>(groupDetailService.getGroupParticipantList(groupUuid), HttpStatus.OK);
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


    @PostMapping("/invite")
    public ResponseEntity<Void> inviteFriend(@RequestBody GroupInviteDto groupInviteDto){
        groupDetailService.inviteFriend(groupInviteDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}