package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.group.GenderOptionsEntity;

@Repository
public interface GenderOptionsRepository extends JpaRepository<GenderOptionsEntity, Long> {
	GenderOptionsEntity findByGenderDescription(String genderDescription);

}
