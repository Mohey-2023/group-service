package com.mohey.groupservice.detail.model.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "gender_options_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenderOptionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gender_uuid", length = 36)
    private String genderUuid;

    @Column(name = "gender_description", length = 255)
    private String genderDescription;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}
