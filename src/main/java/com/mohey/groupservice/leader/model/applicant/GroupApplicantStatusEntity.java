package com.mohey.groupservice.leader.model.applicant;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="group_applicant_status_tb")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplicantStatusEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@Column(name = "group_participant_tb_id", nullable = false)
		private Long groupParticipantTbId;

		@Column(nullable = false)
		private Integer status;

		@Column(name = "created_datetime", nullable = false)
		private LocalDateTime createdDatetime;
}
