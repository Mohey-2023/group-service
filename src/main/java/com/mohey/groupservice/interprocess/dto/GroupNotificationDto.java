package com.mohey.groupservice.interprocess.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupNotificationDto {
	private String topic;
	private String type;
	private String senderUuid;
	private String senderName;
	private List<MemberNotificationDetailDto> memberNotificationDetailDtoList;
	private GroupNotificationDetailDto groupNotificationDetailDto;
}
