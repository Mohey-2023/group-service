package com.mohey.groupservice.detail.model.category;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", length = 255)
    private String tagName;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}