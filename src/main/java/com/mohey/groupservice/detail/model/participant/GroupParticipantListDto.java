package com.mohey.groupservice.detail.model.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantListDto {
    private Long groupId;
    private List<GroupParticipantDto> participants;
}