package com.mohey.groupservice.detail.model.participant;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantPublicStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_participant_tb_id", nullable = false)
    private Long groupParticipantTbId;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}