package com.mohey.groupservice.leader.dto.leader;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupDto {
	private String category;
	private String title;
	private String description;
	private List<String> tags;
	private LocalDateTime groupStartDatetime;
	private boolean privacyYn;
	private Integer maxParticipant;
	private String genderOptions;
	private Integer minAge;
	private Integer maxAge;
	private String locationName;
	private String locationAddress;
	private double lng;
	private double lat;
	private String leaderUuid;
}
