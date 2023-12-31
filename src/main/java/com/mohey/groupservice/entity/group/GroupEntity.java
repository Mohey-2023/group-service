package com.mohey.groupservice.entity.group;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;

@Table(name = "group_tb")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String groupUuid;

    @Column(nullable = false)
    private LocalDateTime createdDatetime;
}