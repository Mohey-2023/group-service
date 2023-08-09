package com.mohey.groupservice.detail.dto;

import com.mohey.groupservice.interprocess.dto.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantDto {
    private String memberUuid;
    private String memberName;
    private String memberGender;
    private String birthDate;
    private String profilePicture;
}
