package com.mohey.groupservice.list.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupApplicantRepository;
import com.mohey.groupservice.repository.GroupCoordinatesRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.repository.GroupTagRepository;

@Service
public class GroupListService {
	private final GroupDetailRepository groupDetailRepository;
	private final GroupModifiableRepository groupModifiableRepository;
	private final GroupTagRepository groupTagRepository;
	private final GroupCoordinatesRepository groupCoordinatesRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final CategoryRepository categoryRepository;
	private final GenderOptionsRepository genderOptionsRepository;
	private final GroupApplicantRepository groupApplicantRepository;

	@Autowired
	public GroupListService(GroupDetailRepository groupDetailRepository,
		GroupModifiableRepository groupModifiableRepository,
		GroupTagRepository groupTagRepository,
		GroupCoordinatesRepository groupCoordinatesRepository,
		GroupParticipantRepository groupParticipantRepository,
		CategoryRepository categoryRepository,
		GenderOptionsRepository genderOptionsRepository,
		GroupApplicantRepository groupApplicantRepository
	){
		this.groupDetailRepository = groupDetailRepository;
		this.groupModifiableRepository = groupModifiableRepository;
		this.groupTagRepository = groupTagRepository;
		this.groupCoordinatesRepository = groupCoordinatesRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.categoryRepository = categoryRepository;
		this.genderOptionsRepository = genderOptionsRepository;
		this.groupApplicantRepository = groupApplicantRepository;
	}


}
