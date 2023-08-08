package com.mohey.groupservice.interprocess.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@ToString
@Data
public class MemberNotificationRequestDto<T> {
	private Integer code;
	private String msg;
	Data data;

	public String getReceiverName(){
		return data.receiverName;
	}

	public List<String> getReceiverToken(){
		return data.receiverToken;
	}

	@ToString
	@Getter
	static class Data {
		String receiverName;
		List<String> receiverToken;
	}
}
