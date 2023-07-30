package com.mohey.groupservice.detail.controller;

import com.mohey.groupservice.detail.model.group.GroupDto;
import com.mohey.groupservice.detail.service.GroupDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{group-id}")
    public ResponseEntity<GroupDto> getGroupDetail(@PathVariable("group-id") Long groupId) {
        // 주어진 groupId를 기반으로 그룹 상세 정보를 조회하는 기능을 호출하고 결과를 ResponseEntity로 반환합니다.
        GroupDto groupDetailDto = groupDetailService.getGroupDetailByGroupId(groupId);
        return ResponseEntity.ok(groupDetailDto);
    }
}