package com.mohey.groupservice.interprocess.dto;

import java.util.List;

import lombok.Data;

@Data
public class MemberNameResponseDto {
	private Integer code;
	private String msg;
	Data data;

	public String getMemberName(){
		return data.memberName;
	}

	static class Data {
		String memberName;
	}
}
