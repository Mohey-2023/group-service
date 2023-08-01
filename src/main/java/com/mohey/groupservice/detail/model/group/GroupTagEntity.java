package com.mohey.groupservice.detail.model.group;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.mohey.groupservice.detail.model.category.TagEntity;

@Table(name = "group_tag_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_description_tb_id", nullable = false)
    private Long groupDescriptionTbId;

    @Column(name = "tag_tb_id", nullable = false)
    private Long tagTbId;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    @ManyToOne
    @JoinColumn(name = "tab_tb_id", nullable = false)
    private TagEntity tagEntity;
}