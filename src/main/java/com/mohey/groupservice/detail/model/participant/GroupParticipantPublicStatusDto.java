package com.mohey.groupservice.detail.model.participant;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantPublicStatusDto {

    private Long id;
    private Long groupParticipantTbId;
    private Integer status;
    private LocalDateTime createdDatetime;
}