package com.mohey.groupservice.list.dto;

import java.util.List;

import com.mohey.groupservice.entity.group.GroupEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendListDto {
	private String friendUuid;
	private GroupEntity groupEntity;
}
