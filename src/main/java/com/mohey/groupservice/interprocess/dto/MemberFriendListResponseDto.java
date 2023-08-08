package com.mohey.groupservice.interprocess.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;

@Data
public class MemberFriendListResponseDto {
	private Integer code;
	private String msg;
	Data data;

	public List<String> getFriendList(){
		return data.friendList;
	}

	static class Data {
		List<String> friendList;
	}
}
