package com.mohey.groupservice.detail.model.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "group_confirm_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupConfirmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}