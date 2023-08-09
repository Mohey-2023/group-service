package com.mohey.groupservice.leader.dto.applicant;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplicantDto {
    private String memberUuid;
    private String memberName;
    private String memberGender;
    private String birthDate;
    private String profilePicture;
}