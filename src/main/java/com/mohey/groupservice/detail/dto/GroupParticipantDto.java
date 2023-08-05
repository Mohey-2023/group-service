package com.mohey.groupservice.detail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupParticipantDto {
    private String memberUuid;
    private String profilePicture;
    private boolean isFavorite;
}
