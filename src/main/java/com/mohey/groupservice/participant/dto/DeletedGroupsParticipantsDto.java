package com.mohey.groupservice.participant.dto;

import java.util.List;

import com.mohey.groupservice.entity.participant.GroupParticipantEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletedGroupsParticipantsDto {
	private List<GroupParticipantEntity> participants;
	private String groupUuid;
}
