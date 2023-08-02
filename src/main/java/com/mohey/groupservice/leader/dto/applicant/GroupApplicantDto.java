package com.mohey.groupservice.leader.dto.applicant;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplicantDto {

    private String memberUuid;
    private String profilePicture;
    private boolean isFavorite;
}