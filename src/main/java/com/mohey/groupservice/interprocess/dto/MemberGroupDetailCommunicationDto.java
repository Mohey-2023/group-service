package com.mohey.groupservice.interprocess.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberGroupDetailCommunicationDto {
	private String memberName;
	private String memberGender;
	private String birthDate;
	private String profilePicture;
}
