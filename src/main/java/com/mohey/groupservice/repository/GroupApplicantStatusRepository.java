package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.applicant.GroupApplicantStatusEntity;

@Repository
public interface GroupApplicantStatusRepository extends JpaRepository<GroupApplicantStatusEntity, Long> {

}
