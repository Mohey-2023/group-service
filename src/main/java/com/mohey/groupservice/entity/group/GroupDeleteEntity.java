package com.mohey.groupservice.entity.group;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "group_delete_tb")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDeleteEntity {
    @Id
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;


}