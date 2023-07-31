package com.mohey.groupservice.leader.model.applicant;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "group_applicant_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplicantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "member_uuid", nullable = false, length = 36)
    private String memberUuid;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    @ManyToOne
    @JoinColumn(name = "id")
    private GroupApplicantStatusEntity applicantStatus;
}