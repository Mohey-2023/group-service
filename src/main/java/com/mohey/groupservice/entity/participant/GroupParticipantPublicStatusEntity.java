package com.mohey.groupservice.entity.participant;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="group_participant_public_status_tb")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantPublicStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_participant_id", nullable = false)
    private Long groupParticipantId;

    @Column(nullable = false)
    private Boolean status;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}