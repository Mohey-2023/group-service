package com.mohey.groupservice.entity.group;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;

@Table(name = "group_tb")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String groupUuid;

    @Column(nullable = false)
    private LocalDateTime createdDatetime;

    @OneToOne
    @JoinColumn(name = "id")
    private GroupDeleteEntity groupDelete;

    @OneToOne
    @JoinColumn(name = "id")
    private GroupConfirmEntity groupConfirm;

    @OneToMany(mappedBy = "groupEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupModifiableEntity> groupModifiableList = new ArrayList<>();
}