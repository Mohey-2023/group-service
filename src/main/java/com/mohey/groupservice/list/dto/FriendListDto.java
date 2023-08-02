package com.mohey.groupservice.list.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendListDto {
	private String myUuid;
	private List<String> friendsUuid;
}
