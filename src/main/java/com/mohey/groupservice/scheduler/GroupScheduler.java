package com.mohey.groupservice.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mohey.groupservice.detail.service.GroupDetailService;
import com.mohey.groupservice.leader.service.GroupLeaderService;

@Component
public class GroupScheduler {
	@Autowired
	private GroupDetailService groupDetailService;
	@Autowired
	private GroupLeaderService groupLeaderService;


	@Scheduled(fixedRate = 60000)
	public void explodeGroup(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startTime = currentDateTime.plusHours(1);
		LocalDateTime endTime = startTime.plusMinutes(1);

		groupDetailService.deleteNotConfirmedGroups(startTime, endTime);
	}

	@Scheduled(fixedRate = 60000)
	public void alertLeaderToConfirm(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startTime = currentDateTime.plusHours(1).plusMinutes(10);
		LocalDateTime endTime = startTime.plusMinutes(1);

		groupLeaderService.alertLeaderToConfirm(startTime, endTime);
	}

	@Scheduled(fixedRate = 60000)
	public void alertGroupRealTimeLocation(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startTime = currentDateTime.plusMinutes(30);
		LocalDateTime endTime = startTime.plusMinutes(1);

		groupLeaderService.alertParticipantRealTimeLocation(startTime, endTime);
	}
}
