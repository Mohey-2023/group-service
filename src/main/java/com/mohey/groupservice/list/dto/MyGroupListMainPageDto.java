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
public class MyGroupListMainPageDto {
	private String title;
	private String category;
	private String groupUuid;
	private LocalDateTime groupStartDatetime;
	private String locationAddress;
	private Double Lng;
	private Double Lat;
	private Integer participantNum;
	private Long remainingSecond;
}
