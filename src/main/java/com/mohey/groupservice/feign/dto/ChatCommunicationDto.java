package com.mohey.groupservice.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatCommunicationDto {
	private String groupUuid;  //생성, 가입, 수정, 퇴장
	private String groupName;  //생성, 수정
	private String groupType;  //생성, 수정
	private String memberUuid;  //생성, 가입, 퇴장
}
