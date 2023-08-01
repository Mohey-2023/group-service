package com.mohey.groupservice.detail.model.category;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "category_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_uuid", length = 36)
    private String categoryUuid;

    @Column(name = "category_name", length = 10)
    private String categoryName;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}