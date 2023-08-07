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
public class MemberNotificationDetailDto {
	private String senderName;
	private String receiverName;
	private List<String> deviceTokenList;
}
