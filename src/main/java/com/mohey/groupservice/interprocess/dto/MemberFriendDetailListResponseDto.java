package com.mohey.groupservice.interprocess.dto;

import lombok.Data;

import java.util.List;
@Data
public class MemberFriendDetailListResponseDto {
    private Integer code;
    private String msg;
    private Data data;

    public List<MemberFriendDetailListDto> getMemberFriendDetailList(){
        return data.memberFriendDetailList;
    }

    static class Data {
        List<MemberFriendDetailListDto> memberFriendDetailList;
    }
}
