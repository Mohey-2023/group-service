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
		LocalDateTime oneHourBefore = currentDateTime.plusHours(1);

		groupDetailService.deleteNotConfirmedGroups(oneHourBefore);
	}

	@Scheduled(fixedRate = 60000)
	public void alertLeaderToConfirm(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime oneHourBefore = currentDateTime.plusHours(1);
		LocalDateTime tenMinsBefore = currentDateTime.plusHours(1).plusMinutes(10);

		groupLeaderService.alertLeaderToConfirm(tenMinsBefore, oneHourBefore);
	}

	@Scheduled(fixedRate = 60000)
	public void alertGroupRealTimeLocation(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime thirtyMinsBefore = currentDateTime.plusMinutes(30);

		groupLeaderService.alertParticipantRealTimeLocation(thirtyMinsBefore);
	}
}
