package com.mohey.groupservice.repository;


import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.list.dto.SearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomGroupDetailRepositoryImpl implements CustomGroupDetailRepository{
    private final JPAQueryFactory queryFactory;

    @Autowired
    public CustomGroupDetailRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<GroupEntity> findGroupsInMapWithDynamicSearch(SearchCondition searchCondition, LocalDateTime currentDatetime, Double swLng, Double swLat, Double neLng, Double neLat) {
        return null;
    }
}

