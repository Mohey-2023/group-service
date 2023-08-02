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
public class CalendarDto {
	private String groupUuid;
	private String title;
	private String Category;
	private Double Lat;
	private Double Lng;
	private String locationId;
	private LocalDateTime groupStartDatetime;
}
