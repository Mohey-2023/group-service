package com.mohey.groupservice.detail.model.participant;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantDto {

    private Long id;
    private Long groupId;
    private String memberUuid;
    private LocalDateTime createdDatetime;
}