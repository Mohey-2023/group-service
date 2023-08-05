package com.mohey.groupservice.component;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mohey.groupservice.detail.service.GroupDetailService;
import com.mohey.groupservice.entity.group.GroupEntity;

@Component
public class GroupScheduler {
//
//	@Autowired
//	private GroupDetailService groupDetailService;
//
//	@Scheduled(fixedRate = 6000)
//	public void explodeGroup(){
//		LocalDateTime currentDateTime = LocalDateTime.now();
//		LocalDateTime oneHourBefore = currentDateTime.minusHours(1);
//
//		groupDetailService.deleteNotConfirmedGroups(oneHourBefore);
//
//	}
}
