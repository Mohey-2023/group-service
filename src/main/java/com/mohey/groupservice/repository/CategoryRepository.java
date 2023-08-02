package com.mohey.groupservice.repository;

import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.category.CategoryEntity;

@Repository
public interface CategoryRepository {
	CategoryEntity findByCategoryUuid(String categoryUuid);
	CategoryEntity findById(Long id);
}
