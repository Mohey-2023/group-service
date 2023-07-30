package com.mohey.groupservice.detail.model.group;

import com.mohey.groupservice.detail.model.applicant.GroupApplicantEntity;
import com.mohey.groupservice.detail.model.category.CategoryEntity;
import com.mohey.groupservice.detail.model.participant.GroupParticipantEntity;
import com.mohey.groupservice.detail.model.participant.GroupParticipantStatusEntity;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "group_tb")
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

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private String groupDescription;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "gender_option_id", nullable = false)
    private GenderOptionsEntity genderOptions;

    @Column(nullable = false)
    private LocalDateTime groupStartDatetime;

    @Column(nullable = false)
    private Integer maxParticipant;

    @Column(nullable = false)
    private String leaderUuid;

    @Column(nullable = false)
    private boolean privateYn;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column
    private Integer minAge;

    @Column
    private Integer maxAge;

    @ManyToOne
    @JoinColumn(name = "groupConfirmId")
    private GroupConfirmEntity groupConfirm;

    @ManyToOne
    @JoinColumn(name = "groupDeleteId")
    private GroupDeleteEntity groupDelete;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupApplicantEntity> groupApplicants;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupParticipantEntity> groupParticipants;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupTagEntity> groupTags = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupParticipantStatusEntity> participantStatusList = new ArrayList<>();

    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GroupCoordinatesEntity groupCoordinate;
}