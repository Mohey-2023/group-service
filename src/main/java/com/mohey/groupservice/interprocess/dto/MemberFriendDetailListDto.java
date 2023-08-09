package com.mohey.groupservice.interprocess.dto;

import lombok.Data;

@Data
public class MemberFriendDetailListDto {
    private String memberUuid;

    private String nickname;

    private GenderEnum gender;

    private String birthDate;

    private String profileUrl;

    private Boolean favoriteStatus;
}
