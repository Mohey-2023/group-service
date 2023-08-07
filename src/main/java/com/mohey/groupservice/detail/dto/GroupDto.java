package com.mohey.groupservice.detail.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    private Long groupId;
    private Integer participantsNum;
    private String description;
    private String category;
    private String title;
    private LocalDateTime groupStartDatetime;
    private Integer maxParticipant;
    private String leaderUuid;
    private String locationName;
    private String locationAddress;
    private double lng;
    private double lat;
    private String genderOptions;
    private Integer minAge;
    private Integer maxAge;
    private List<String> tags;
    private String profilePicture1;
    private String profilePicture2;
    private String profilePicture3;
}