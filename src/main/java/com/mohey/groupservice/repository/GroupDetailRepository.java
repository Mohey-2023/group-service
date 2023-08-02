package com.mohey.groupservice.repository;


import com.mohey.groupservice.entity.group.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDetailRepository extends JpaRepository<GroupEntity, Long> {
    GroupEntity findByGroupUuid(String groupUuid);

    GroupEntity findByGroupId(Long groupId);

    @Query("SELECT g FROM GroupEntity g " +
            "JOIN g.groupModifiableList gm " +
            "WHERE YEAR(gm.groupStartDatetime) = :year AND MONTH(gm.groupStartDatetime) = :month " +
            "AND g.id = :groupId " +
            "AND gm.latestYn = true")
    GroupEntity findGroupByYearAndMonthAndGroupId(@Param("year") int year, @Param("month") int month, @Param("groupId") Long groupId);
}
