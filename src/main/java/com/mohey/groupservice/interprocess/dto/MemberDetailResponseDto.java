package com.mohey.groupservice.interprocess.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
public class MemberDetailResponseDto {
    private Integer code;
    private String msg;
    private MemberGroupDetailCommunicationDto data;

    public MemberGroupDetailCommunicationDto getMemberDetailList() {
        return data;
    }

}
