package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mohey.groupservice.entity.category.TagEntity;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
	TagEntity findByTagName(String tagName);
}
