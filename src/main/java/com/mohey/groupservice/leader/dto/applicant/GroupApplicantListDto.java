package com.mohey.groupservice.leader.dto.applicant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplicantListDto {
    private Long groupId;
    private List<GroupApplicantDto> applicants;
}