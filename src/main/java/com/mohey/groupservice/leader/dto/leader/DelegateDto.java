package com.mohey.groupservice.leader.dto.leader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DelegateDto {
    private Long groupId;
    private String leaderUuid;
    private String delegatedUuid;
}
