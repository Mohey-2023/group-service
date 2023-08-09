package com.mohey.groupservice.list.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendGroupDto {
	private String title;
	private String name;
	private String groupUuid;
	private String category;
	private LocalDateTime groupStartDatetime;
	private String locationAddress;
	private Integer participantNum;
	private Integer maxParticipantNum;
}
