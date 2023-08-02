package com.mohey.groupservice.participant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinLeaveDto {
	private String GroupUuid;
	private String MemberUuid;
}
