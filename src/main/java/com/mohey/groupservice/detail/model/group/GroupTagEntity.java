package com.mohey.groupservice.detail.model.group;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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
}