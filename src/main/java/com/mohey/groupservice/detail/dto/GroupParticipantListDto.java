package com.mohey.groupservice.detail.dto;

import com.mohey.groupservice.interprocess.dto.MemberFriendDetailListDto;
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
    String groupUuid;
    List<GroupParticipantDto> participants;
}
