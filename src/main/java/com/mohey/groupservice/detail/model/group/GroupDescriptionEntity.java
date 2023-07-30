package com.mohey.groupservice.detail.model.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDescriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_modifiable_id", nullable = false)
    private Long groupModifiableId;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}