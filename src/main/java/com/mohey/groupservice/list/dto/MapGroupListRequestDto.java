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
	Double neLat;
	Double neLng;
	Double swlat;
	Double swlng;
}