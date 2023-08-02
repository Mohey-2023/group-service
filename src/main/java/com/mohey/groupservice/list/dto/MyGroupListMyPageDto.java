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
public class MyGroupListMyPageDto {
	private String groupUuid;
	private String title;
	private String category;
	private LocalDateTime groupStartDatetime;
	private String locationId;
	private Double lat;
	private Double Lng;
	private Boolean isPrivate;
}
