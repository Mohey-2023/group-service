package com.mohey.groupservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.applicant.GroupApplicantEntity;

@Repository
public interface GroupApplicantRepository extends JpaRepository<GroupApplicantEntity, Long> {
	@Query("SELECT ga FROM GroupApplicantEntity ga LEFT JOIN GroupApplicantStatusEntity ase ON ga.id = ase.id WHERE ga.groupId = :groupId AND ase.createdDatetime IS NULL")
	List<GroupApplicantEntity> findByGroupIdApplicantsWithNoStatus(@Param("groupId") Long groupId);

	@Query("SELECT ga FROM GroupApplicantEntity ga LEFT JOIN GroupApplicantStatusEntity ase ON ga.id = ase.id WHERE ga.groupId = :groupId AND ga.memberUuid = :memberUuid AND ase.createdDatetime IS NULL")
	GroupApplicantEntity findByGroupIdAndMemberUuidApplicantsWithNoStatus(@Param("groupId") Long groupId, @Param("memberUuid") String memberUuid);
}
