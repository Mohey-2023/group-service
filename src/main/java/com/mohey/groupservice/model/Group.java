package com.mohey.groupservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Group{
	@Id
	private String groupId;
}
