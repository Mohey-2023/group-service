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
    private String groupUuid;
    private String leaderUuid;
    private String delegatedUuid;
}
