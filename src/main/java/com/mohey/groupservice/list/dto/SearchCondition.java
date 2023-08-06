package com.mohey.groupservice.list.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCondition {
    private String titleKeyword;
    private String genderOptionsUuid;
    private String categoryUuid;
    private Integer minAge;
    private Integer maxAge;
}
