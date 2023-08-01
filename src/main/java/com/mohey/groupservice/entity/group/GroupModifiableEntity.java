package com.mohey.groupservice.entity.group;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mohey.groupservice.entity.category.CategoryEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "group_modifiable_tb")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupModifiableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long groupTbId;

	@Column(nullable = false)
	private Long categoryTbId;

	@Column(nullable = false)
	private Long genderOptionsTbId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private LocalDateTime groupStartDatetime;

	@Column(nullable = false)
	private Integer maxParticipant;

	@Column(nullable = false)
	private String leaderUuid;

	@Column(nullable = false)
	private boolean privateYn;

	@Column(nullable = false)
	private double lat;

	@Column(nullable = false)
	private double lng;

	@Column
	private Integer minAge;

	@Column
	private Integer maxAge;

	@Column(nullable = false)
	private boolean latestYn;

	@Column(nullable = false, length = 255)
	private String description;

	@Column(nullable = false)
	private LocalDateTime createdDatetime;

}
