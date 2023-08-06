package com.mohey.groupservice.repository;

import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.list.dto.SearchCondition;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomGroupDetailRepository {
    List<GroupEntity> findGroupsInMapWithDynamicSearch(SearchCondition searchCondition, LocalDateTime currentDatetime, Double swLng, Double swLat, Double neLng, Double neLat);
}

