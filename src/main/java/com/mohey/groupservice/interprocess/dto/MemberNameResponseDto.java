package com.mohey.groupservice.interprocess.dto;

import lombok.Data;

@Data
public class MemberNameResponseDto {
	private Integer code;
	private String msg;
	Data data;

	public String getMemberName(){
		return data.memberName;
	}

	@lombok.Data
	static class Data {
		String memberName;

		public Data(String memberName) {
			this.memberName = memberName;
		}
	}
}
