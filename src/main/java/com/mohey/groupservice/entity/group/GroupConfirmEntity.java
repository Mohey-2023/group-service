package com.mohey.groupservice.entity.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "group_confirm_tb")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupConfirmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    @OneToOne
    @JoinColumn(name = "id")
    private GroupConfirmEntity groupConfirm;
}