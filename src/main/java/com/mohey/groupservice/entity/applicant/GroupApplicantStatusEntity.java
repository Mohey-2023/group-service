package com.mohey.groupservice.entity.applicant;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;

@Table(name="group_applicant_status_tb")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplicantStatusEntity {
		@Id
		private Long id;

		@Column(nullable = false)
		private Boolean applicantStatus;

		@Column(name = "created_datetime", nullable = false)
		private LocalDateTime createdDatetime;
}
