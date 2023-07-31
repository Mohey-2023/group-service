package com.mohey.groupservice.leader.model.applicant;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplicantDto {

    private Long id;
    private Long groupId;
    private String memberUuid;
    private LocalDateTime createdDatetime;
}