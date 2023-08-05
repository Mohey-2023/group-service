package com.mohey.groupservice.participant.controller;

import com.mohey.groupservice.participant.service.GroupParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class GroupParticipantController {

    private final GroupParticipantService groupParticipantService;

    @Autowired
    public GroupParticipantController(GroupParticipantService groupParticipantService) {
        this.groupParticipantService = groupParticipantService;
    }

    // @PostMapping("/join")
    // public ResponseEntity<String> joinGroup(@RequestParam Long groupId, @RequestParam String participantId) {
    //     groupParticipantService.joinGroup(groupId, participantId);
    //     return ResponseEntity.ok("Successfully joined the group.");
    // }
    //
    // @PostMapping("/leave")
    // public ResponseEntity<String> leaveGroup(@RequestParam Long groupId, @RequestParam String participantId) {
    //     groupParticipantService.leaveGroup(groupId, participantId);
    //     return ResponseEntity.ok("Successfully left the group.");
    // }
}