package com.mohey.groupservice.detail.model.group;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    private String groupUuid;
    private Integer participantsNum;
    private String groupDescription;
    private String category;
    private LocalDateTime groupStartDatetime;
    private Integer maxParticipant;
    private String leaderUuid;
    private String locationId;
    private double lng;
    private double lat;
    private String genderOptions;
    private Integer minAge;
    private Integer maxAge;
    private List<String> tags;
}