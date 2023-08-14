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
        "AND gc.createdDatetime IS NOT NULL " +
        "AND gd.createdDatetime IS NULL")
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
        "AND gm.groupStartDatetime > :currentDatetime " +
        "ORDER BY gm.groupStartDatetime DESC")
    List<GroupEntity> findFutureConfirmedGroupsForParticipant(@Param("memberUuid") String memberUuid, @Param("currentDatetime") LocalDateTime currentDatetime);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gc.createdDatetime IS NULL " +
        "AND gm.latestYn = true " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gm.groupStartDatetime > :currentDatetime " +
        "ORDER BY gm.groupStartDatetime DESC")
    List<GroupEntity> findFutureNotConfirmedGroupsForParticipant(@Param("memberUuid") String memberUuid, @Param("currentDatetime") LocalDateTime currentDatetime);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gm.latestYn = true " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gm.groupStartDatetime > :currentDatetime " +
        "ORDER BY gm.groupStartDatetime DESC")
    List<GroupEntity> findAllFutureGroupsForParticipant(@Param("memberUuid") String memberUuid, @Param("currentDatetime") LocalDateTime currentDatetime);


    @Query("SELECT g FROM GroupEntity g " +
            "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
            "WHERE gm.latestYn = true " +
            "AND gc.createdDatetime IS NULL " +
            "AND gd.createdDatetime IS NULL " +
            "AND gm.groupStartDatetime < :endTime " +
            "AND gm.groupStartDatetime >= :startTime")
    List<GroupEntity> findGroupsToBeDeleted(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gm.latestYn = true " +
        "AND gc.createdDatetime IS NOT NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gm.groupStartDatetime < :endTime " +
        "AND gm.groupStartDatetime >= :startTime")
    List<GroupEntity> findGroupsRealTimeLocation(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gm.latestYn = true " +
        "AND gc.createdDatetime IS NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gm.groupStartDatetime < :endTime " +
        "AND gm.groupStartDatetime >= :startTime")
    List<GroupEntity> findGroupsNeedConfirm(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT g FROM GroupEntity g " +
            "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
            "WHERE gm.latestYn = true " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid " +
        "ORDER BY gm.groupStartDatetime DESC")
    List<GroupEntity> findAllGroupsForParticipant(@Param("memberUuid") String memberUuid);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gm.latestYn = true " +
        "AND gc.createdDatetime IS NOT NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gm.groupStartDatetime < :currentDatetime " +
        "ORDER BY gm.groupStartDatetime DESC")
    List<GroupEntity> findMyPastGroups(@Param("memberUuid") String memberUuid,
        @Param("currentDatetime")LocalDateTime currentDatetime);

    @Query("SELECT g FROM GroupEntity g " +
        "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
        "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
        "LEFT JOIN GroupParticipantStatusEntity gps ON gp.id = gps.id " +
        "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
        "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
        "WHERE gm.latestYn = true " +
        "AND gm.privateYn = true " +
        "AND gc.createdDatetime IS NOT NULL " +
        "AND gd.createdDatetime IS NULL " +
        "AND gps.createdDatetime IS NULL " +
        "AND gp.memberUuid = :memberUuid " +
        "AND gm.groupStartDatetime < :currentDatetime " +
        "ORDER BY gm.groupStartDatetime DESC")
    List<GroupEntity> findOthersPastGroups(@Param("memberUuid") String memberUuid,
        @Param("currentDatetime")LocalDateTime currentDatetime);

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
        "AND (:genderOptionsTbId IS NULL OR gm.genderOptionsTbId = :genderOptionsTbId) " +
        "AND (:categoryTbId IS NULL OR gm.categoryTbId = :categoryTbId) " +
        "AND (:minAge IS NULL OR gm.minAge >= :minAge) " +
        "AND (:maxAge IS NULL OR gm.maxAge <= :maxAge)")
    List<GroupEntity> findGroupsInMap(@Param("currentDatetime") LocalDateTime currentDatetime,
        @Param("swLng") Double swLng, @Param("swLat") Double swLat,
        @Param("neLng") Double neLng, @Param("neLat") Double neLat,
        @Param("titleKeyword") String titleKeyword,
        @Param("genderOptionsTbId") Long genderOptionsTbId,
        @Param("categoryTbId") Long categoryTbId,
        @Param("minAge") Integer minAge,
        @Param("maxAge") Integer maxAge);

    @Query("SELECT g FROM GroupEntity g " +
            "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
            "LEFT JOIN GroupTagEntity gt ON gt.groupModifiableTbId = gm.id " +
            "WHERE gm.latestYn = true " +
            "AND gc.createdDatetime IS NULL " +
            "AND gd.createdDatetime IS NULL " +
            "AND gm.groupStartDatetime > :currentDatetime " +
            "AND gm.lat < :neLat AND gm.lat > :swLat " +
            "AND gm.lng < :neLng AND gm.lng > :swLng " +
            "AND gt.tagTbId = :tagId " +
            "AND (:genderOptionsTbId IS NULL OR gm.genderOptionsTbId = :genderOptionsTbId) " +
            "AND (:categoryTbId IS NULL OR gm.categoryTbId = :categoryTbId) " +
            "AND (:minAge IS NULL OR gm.minAge >= :minAge) " +
            "AND (:maxAge IS NULL OR gm.maxAge <= :maxAge)")
    List<GroupEntity> findGroupsInMapByTag(@Param("currentDatetime") LocalDateTime currentDatetime,
                                      @Param("swLng") Double swLng, @Param("swLat") Double swLat,
                                      @Param("neLng") Double neLng, @Param("neLat") Double neLat,
                                      @Param("tagId") Long tagId,
                                      @Param("genderOptionsTbId") Long genderOptionsTbId,
                                      @Param("categoryTbId") Long categoryTbId,
                                      @Param("minAge") Integer minAge,
                                      @Param("maxAge") Integer maxAge);

    @Query("SELECT DISTINCT g FROM GroupEntity g " +
            "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
            "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
            "WHERE gm.latestYn = true " +
            "AND gc.createdDatetime IS NULL " +
            "AND gd.createdDatetime IS NULL " +
            "AND gm.groupStartDatetime > :currentDatetime " +
            "AND gm.lat < :neLat AND gm.lat > :swLat " +
            "AND gm.lng < :neLng AND gm.lng > :swLng " +
            "AND (:titleKeyword IS NULL OR gm.title LIKE %:titleKeyword%) " +
            "AND (:genderOptionsTbId IS NULL OR gm.genderOptionsTbId = :genderOptionsTbId) " +
            "AND (:categoryTbId IS NULL OR gm.categoryTbId = :categoryTbId) " +
            "AND (:minAge IS NULL OR gm.minAge >= :minAge) " +
            "AND (:maxAge IS NULL OR gm.maxAge <= :maxAge) " +
            "AND gp.memberUuid IN :friendUuids")
    List<GroupEntity> findFriendsGroupsInMap(@Param("currentDatetime") LocalDateTime currentDatetime,
                                             @Param("swLng") Double swLng, @Param("swLat") Double swLat,
                                             @Param("neLng") Double neLng, @Param("neLat") Double neLat,
                                             @Param("titleKeyword") String titleKeyword,
                                             @Param("genderOptionsTbId") Long genderOptionsTbId,
                                             @Param("categoryTbId") Long categoryTbId,
                                             @Param("minAge") Integer minAge,
                                             @Param("maxAge") Integer maxAge,
                                             @Param("friendUuids") List<String> friendUuids);

    @Query("SELECT DISTINCT g FROM GroupEntity g " +
            "JOIN GroupModifiableEntity gm ON g.id = gm.groupId "+
            "LEFT JOIN GroupConfirmEntity gc ON g.id = gc.id " +
            "LEFT JOIN GroupDeleteEntity gd ON g.id = gd.id " +
            "LEFT JOIN GroupParticipantEntity gp ON g.id = gp.groupId " +
            "LEFT JOIN GroupTagEntity gt ON gt.groupModifiableTbId = gm.id " +
            "WHERE gm.latestYn = true " +
            "AND gc.createdDatetime IS NULL " +
            "AND gd.createdDatetime IS NULL " +
            "AND gm.groupStartDatetime > :currentDatetime " +
            "AND gm.lat < :neLat AND gm.lat > :swLat " +
            "AND gm.lng < :neLng AND gm.lng > :swLng " +
            "AND gt.tagTbId = :tagId " +
            "AND (:genderOptionsTbId IS NULL OR gm.genderOptionsTbId = :genderOptionsTbId) " +
            "AND (:categoryTbId IS NULL OR gm.categoryTbId = :categoryTbId) " +
            "AND (:minAge IS NULL OR gm.minAge >= :minAge) " +
            "AND (:maxAge IS NULL OR gm.maxAge <= :maxAge) " +
            "AND gp.memberUuid IN :friendUuids")
    List<GroupEntity> findFriendsGroupsInMapByTag(@Param("currentDatetime") LocalDateTime currentDatetime,
                                             @Param("swLng") Double swLng, @Param("swLat") Double swLat,
                                             @Param("neLng") Double neLng, @Param("neLat") Double neLat,
                                                  @Param("tagId") Long tagId,
                                                  @Param("genderOptionsTbId") Long genderOptionsTbId,
                                                  @Param("categoryTbId") Long categoryTbId,
                                             @Param("minAge") Integer minAge,
                                             @Param("maxAge") Integer maxAge,
                                             @Param("friendUuids") List<String> friendUuids);
}
