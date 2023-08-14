package com.mohey.groupservice.interprocess.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mohey.groupservice.util.CustomLocalDateTimeDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberGroupDetailCommunicationDto {
	private String memberName;
	private String memberGender;



	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime birthDate;
	private String profilePicture;

}
