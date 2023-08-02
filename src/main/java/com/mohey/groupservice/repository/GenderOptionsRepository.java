package com.mohey.groupservice.repository;

import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.group.GenderOptionsEntity;

@Repository
public interface GenderOptionsRepository {
	GenderOptionsEntity findByGenderUuid(String genderUuid);

	GenderOptionsEntity findById(Long id);
}
