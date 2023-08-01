package com.mohey.groupservice.entity.group;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

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



}