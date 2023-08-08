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
	private GroupLeaderService groupLeaderService;

	@Scheduled(fixedRate = 30000)
	public void explodeGroup(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime oneHourBefore = currentDateTime.minusHours(1);

		groupDetailService.deleteNotConfirmedGroups(oneHourBefore);
	}

	@Scheduled(fixedRate = 30000)
	public void alertLeaderToConfirm(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime oneHourBefore = currentDateTime.minusHours(1);
		LocalDateTime tenMinsBefore = currentDateTime.minusHours(1).minusMinutes(10);

		groupLeaderService.alertLeaderToConfirm(tenMinsBefore, oneHourBefore);
	}

	@Scheduled(fixedRate = 30000)
	public void alertGroupRealTimeLocation(){
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime thirtyMinsBefore = currentDateTime.minusMinutes(30);

		groupLeaderService.alertParticipantRealTimeLocation(thirtyMinsBefore);
	}
}
