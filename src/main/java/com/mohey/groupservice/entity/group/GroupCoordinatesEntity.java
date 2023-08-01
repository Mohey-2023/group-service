package com.mohey.groupservice.entity.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "group_coordinates_id_tb")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCoordinatesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_tb_id", nullable = false)
    private Long groupTbId;

    @Column(name = "location_id", length = 50)
    private String locationId;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}