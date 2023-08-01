package com.mohey.groupservice.detail.model.group;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.mohey.groupservice.detail.model.category.CategoryEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "group_modifiable_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupModifiableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String groupTbId;

	@Column(nullable = false)
	private String categoryTbId;

	@Column(nullable = false)
	private String genderOptionsTbId;

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

	@Column(nullable = false)
	private LocalDateTime createdDatetime;

	@ManyToOne
	@JoinColumn(name = "category_tb_id", nullable = false)
	private CategoryEntity category;

	@ManyToOne
	@JoinColumn(name = "gender_options_tb_id", nullable = false)
	private GenderOptionsEntity genderOptions;
}
