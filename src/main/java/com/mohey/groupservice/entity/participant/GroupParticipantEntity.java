package com.mohey.groupservice.entity.participant;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="group_participant_tb")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "member_uuid", nullable = false, length = 36)
    private String memberUuid;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    @OneToOne
    @JoinColumn(name = "id")
    private GroupParticipantStatusEntity groupParticipantStatusEntity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupParticipantPublicStatusEntity> participantPublicStatusList = new ArrayList<>();
}