package com.mohey.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohey.groupservice.model.Group;

public interface GroupRepository extends JpaRepository<Group, String> {

}
