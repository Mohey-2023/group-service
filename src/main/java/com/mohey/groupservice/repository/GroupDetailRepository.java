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

    Optional<GroupEntity> findById(Long id);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId " +
            "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
            "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE YEAR(gm.groupStartDatetime) = :year AND MONTH(gm.groupStartDatetime) = :month " +
            "AND gm.latestYn = true " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gps.createdDatetime IS NULL " +
        "AND gc.createdDatetime IS NULL " +
        "AND gd.createdDatetime IS NOT NULL")
    List<GroupEntity> findGroupsByYearAndMonthForParticipant(@Param("year") int year, @Param("month") int month, @Param("memberUuid") String memberUuid);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gc.createdDatetime IS NOT NULL " +
            "AND gm.latestYn = true " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gm.groupStartDatetime > :currentDatetime")
    List<GroupEntity> findFutureConfirmedGroupsForParticipant(@Param("memberUuid") String memberUuid, @Param("currentDatetime") LocalDateTime currentDatetime);

    @Query("SELECT g FROM GroupEntity g " +
            "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
            "WHERE gm.latestYn = true " +
            "AND gc.createdDatetime IS NULL " +
            "AND gd.createdDatetime IS NULL " +
        "AND gm.groupStartDatetime <= :deleteDatetime")
    List<GroupEntity> findGroupsToBeDeleted(@Param("deleteDatetime") LocalDateTime deleteDatetime);

    @Query("SELECT g FROM GroupEntity g " +
            "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
            "WHERE gm.latestYn = true " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid")
    List<GroupEntity> findAllGroupsForParticipant(@Param("memberUuid") String memberUuid);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gm.latestYn = true " +
        "AND gc.createdDatetime IS NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gm.groupStartDatetime > :currentDatetime " +
        "AND gm.lat < :neLat AND gm.lat > :swLat " +
        "AND gm.lng < :neLng AND gm.lng > :swLng " +
        "AND (:titleKeyword IS NULL OR gm.title LIKE %:titleKeyword%) " +
        "AND (:genderOptionsUuid IS NULL OR gm.genderOptionsTbId = :genderOptionsUuid) " +
        "AND (:categoryUuid IS NULL OR gm.categoryTbId = :categoryUuid) " +
        "AND (:minAge IS NULL OR gm.minAge >= :minAge) " +
        "AND (:maxAge IS NULL OR gm.maxAge <= :maxAge)")
    List<GroupEntity> findGroupsInMap(@Param("currentDatetime") LocalDateTime currentDatetime,
        @Param("swLng") Double swLng, @Param("swLat") Double swLat,
        @Param("neLng") Double neLng, @Param("neLat") Double neLat,
        @Param("titleKeyword") String titleKeyword,
        @Param("genderOptionsUuid") String genderOptionsUuid,
        @Param("categoryUuid") String categoryUuid,
        @Param("minAge") Integer minAge,
        @Param("maxAge") Integer maxAge);

}
