package com.mohey.groupservice.entity.participant;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mohey.groupservice.entity.group.GroupEntity;

@Table(name="group_participant_tb")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_tb_id")
    private GroupEntity groupEntity;

    @OneToOne
    @JoinColumn(name = "id")
    private GroupParticipantStatusEntity groupParticipantStatusEntity;

    @OneToMany(mappedBy = "groupParticipantEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupParticipantPublicStatusEntity> participantPublicStatusList = new ArrayList<>();
}