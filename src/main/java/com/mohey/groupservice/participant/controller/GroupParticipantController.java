package com.mohey.groupservice.participant.controller;

import com.mohey.groupservice.participant.dto.JoinLeaveDto;
import com.mohey.groupservice.participant.service.GroupParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupParticipantController {

    private final GroupParticipantService groupParticipantService;

    @Autowired
    public GroupParticipantController(GroupParticipantService groupParticipantService) {
        this.groupParticipantService = groupParticipantService;
    }

     @PostMapping("/join")
     public ResponseEntity<Void> joinGroup(@RequestBody JoinLeaveDto joinLeaveDto) {
         groupParticipantService.joinGroup(joinLeaveDto);
         return new ResponseEntity<>(HttpStatus.OK);
     }

     @PostMapping("/leave")
     public ResponseEntity<Void> leaveGroup(@RequestBody JoinLeaveDto joinLeaveDto) {
         groupParticipantService.leaveGroup(joinLeaveDto);
         return new ResponseEntity<>(HttpStatus.OK);
     }
}