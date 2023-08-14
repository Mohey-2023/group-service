package com.mohey.groupservice.list.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapGroupListRequestDto {
	private Double neLat;
	private Double neLng;
	private Double swLat;
	private Double swLng;

	private String titleKeyword;
	private String genderOptions;
	private String category;
	private Integer minAge;
	private Integer maxAge;
	private Boolean isFriend;
	private String memberUuid;
}
