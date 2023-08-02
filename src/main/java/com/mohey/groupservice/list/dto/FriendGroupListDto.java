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
public class FriendGroupListDto {
	private String title;
	private String name;
	private String groupUuid;
	private String category;
	private LocalDateTime groupStartDatetime;
	private Double Lat;
	private Double Lng;
	private Integer participantNum;
	private Integer maxParticipantNum;
}
