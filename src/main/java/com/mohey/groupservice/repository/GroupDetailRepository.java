package com.mohey.groupservice.repository;


import com.mohey.groupservice.entity.group.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupDetailRepository extends JpaRepository<GroupEntity, Long> {
    GroupEntity findByGroupUuid(String groupUuid);

    @Query("SELECT DISTINCT g FROM GroupEntity g " +
        "JOIN g.groupModifiableList gm " +
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE YEAR(gm.groupStartDatetime) = :year AND MONTH(gm.groupStartDatetime) = :month " +
        "AND gm.latestYn = true " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gps.createdDatetime IS NULL " +
        "AND gc.createdDatetime IS NOT NULL " +
        "AND gd.createdDatetime IS NULL")
    List<GroupEntity> findGroupsByYearAndMonthForParticipant(@Param("year") int year, @Param("month") int month, @Param("memberUuid") String memberUuid);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN g.groupModifiableList gm " +
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "WHERE gm.latestYn = true " +
        "AND gc.createdDatetime IS NOT NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gm.groupStartDatetime > :currentDatetime")
    List<GroupEntity> findFutureConfirmedGroupsForParticipant(@Param("memberUuid") String memberUuid, @Param("currentDatetime") LocalDateTime currentDatetime);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN g.groupModifiableList gm " +
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gm.latestYn = true " +
        "AND gc.createdDatetime IS NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gm.groupStartDatetime <= :deleteDatetime")
    List<GroupEntity> findGroupsToBeDeleted(@Param("deleteDatetime") LocalDateTime deleteDatetime);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN g.groupModifiableList gm " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "WHERE gm.latestYn = true " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid")
    List<GroupEntity> findAllGroupsForParticipant(@Param("memberUuid") String memberUuid);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN g.groupModifiableList gm " +
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "WHERE gm.latestYn = true " +
        "AND gc.createdDatetime IS NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gm.groupStartDatetime > :currentDatetime " +
        "AND gm.lat BETWEEN :neLat AND :swLat " +
        "AND gm.lng BETWEEN :neLng AND :swLng")
    List<GroupEntity> findGroupsInMap(@Param("currentDatetime") LocalDateTime currentDatetime,
        @Param("swLng") Double swLng, @Param("swLat") Double swLat, @Param("neLng") Double neLng, @Param("neLat") Double neLat);
}
