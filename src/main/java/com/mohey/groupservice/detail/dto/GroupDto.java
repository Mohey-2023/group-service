package com.mohey.groupservice.detail.dto;

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
    private String description;
    private String category;
    private String title;
    private LocalDateTime startDatetime;
    private Integer maxParticipant;
    private String leaderName;
    private String locationName;
    private String locationAddress;
    private double lng;
    private double lat;
    private String genderOptions;
    private List<String> tags;
    private Boolean isMember;
    private Boolean isLeader;
    private Boolean isApplicant;
    private Boolean isConfirmed;
    private Boolean isRealtimePossible;
}