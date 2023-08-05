package com.mohey.groupservice.detail.controller;

import com.mohey.groupservice.detail.dto.GroupDto;
import com.mohey.groupservice.detail.dto.GroupParticipantListDto;
import com.mohey.groupservice.detail.dto.PublicStatusDto;
import com.mohey.groupservice.detail.service.GroupDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{groupUuid}/participants")
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
}