package com.mohey.groupservice.entity.group;

import java.time.LocalDateTime;

import javax.persistence.*;

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
	private Boolean privateYn;

	@Column(nullable = false)
	private Double lat;

	@Column(nullable = false)
	private Double lng;

	@Column
	private Integer minAge;

	@Column
	private Integer maxAge;

	@Column(nullable = false)
	private Boolean latestYn;

	@Column(nullable = false, length = 255)
	private String description;

	@Column(nullable = false, length = 50)
	private String locationName;

	@Column(nullable = false, length = 50)
	private String locationAddress;

	@Column(nullable = false)
	private LocalDateTime createdDatetime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_tb_id")
	private GroupEntity groupEntity;
}
