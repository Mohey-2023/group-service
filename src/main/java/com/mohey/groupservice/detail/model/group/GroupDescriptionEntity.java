package com.mohey.groupservice.detail.model.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "group_description_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDescriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_modifiable_tb_id", nullable = false)
    private Long groupModifiableTbId;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupTagEntity> groupTags = new ArrayList<>();
}