package com.mohey.groupservice.interprocess.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberGroupDetailCommunicationDto {
	private String memberName;
	private String memberGender;
	private LocalDateTime birthDate;
	private String profilePicture;
}
