package com.mohey.groupservice.interprocess.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString
public class MemberDetailResponseDto {
	private Integer code;
	private String msg;
	Data data;

	public MemberGroupDetailCommunicationDto getMemberDetailList(){
		System.out.println(this);
		return data.memberDetailList;
	}

	static class Data {
		MemberGroupDetailCommunicationDto memberDetailList;
	}
}
