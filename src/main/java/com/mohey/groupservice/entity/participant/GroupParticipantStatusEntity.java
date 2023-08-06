package com.mohey.groupservice.entity.participant;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="group_participant_status_tb")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantStatusEntity {

    @Id
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}