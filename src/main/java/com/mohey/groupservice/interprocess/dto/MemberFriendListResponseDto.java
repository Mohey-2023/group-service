package com.mohey.groupservice.interprocess.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class MemberFriendListResponseDto {
	private Integer code;
	private String msg;
	List<Data> data;

	public List<String> getFriendList(){
		List<String> friendsUuidList = new ArrayList<>();
		for (Data datum : data) {
			friendsUuidList.add(datum.memberUuid);
		}

		return friendsUuidList;
	}

	@lombok.Data
	@AllArgsConstructor
	static class Data {
		private String memberUuid;
		private String nickname;
		private GenderEnum gender;
		private String birthDate;
		private String profileUrl;
		private Boolean favoriteStatus;
	}
}
