package com.mohey.groupservice.interprocess.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberGroupDetailCommunicationDto {
	private String memberName;
	private String memberGender;
	private String birthDate;
	private String profilePicture;
}
