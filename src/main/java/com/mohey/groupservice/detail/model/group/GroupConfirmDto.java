package com.mohey.groupservice.detail.model.group;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupConfirmDto {

    private Long id;
    private LocalDateTime createdDatetime;
}