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
public class MyFutureGroupListMyPageDto {
	private String groupUuid;
	private String title;
	private String category;
	private LocalDateTime groupStartDatetime;
	private String locationAddress;
	private Double lat;
	private Double Lng;
	private Boolean isConfirmed;
	private Boolean isPublic;
}
