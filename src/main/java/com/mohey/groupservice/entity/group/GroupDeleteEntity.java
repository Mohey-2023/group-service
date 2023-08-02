package com.mohey.groupservice.entity.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "group_delete_tb")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;


}