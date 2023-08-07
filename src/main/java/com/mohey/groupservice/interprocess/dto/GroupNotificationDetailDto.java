package com.mohey.groupservice.interprocess.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupNotificationDetailDto {
	private String groupUuid;
	private String groupName;
}
