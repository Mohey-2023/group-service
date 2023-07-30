package com.mohey.groupservice.detail.model.group;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupTagDto {

    private Long groupDescriptionTbId;
    private Long tagTbId;
    private LocalDateTime createdDatetime;
}