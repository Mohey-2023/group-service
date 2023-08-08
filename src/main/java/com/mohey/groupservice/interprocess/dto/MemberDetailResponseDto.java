package com.mohey.groupservice.interprocess.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;

@Data
public class MemberDetailResponseDto {
	private Integer code;
	private String msg;
	Data data;

	public List<MemberGroupDetailCommunicationDto> getMemberDetailList(){
		return data.memberDetailList;
	}

	static class Data {
		List<MemberGroupDetailCommunicationDto> memberDetailList;
	}
}
