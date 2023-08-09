package com.mohey.groupservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohey.groupservice.interprocess.dto.GroupNotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public GroupNotificationDto send(String topic, GroupNotificationDto groupNotificationDto) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = "";
		try {
			jsonInString = mapper.writeValueAsString(groupNotificationDto);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
		kafkaTemplate.send(topic, jsonInString);
		log.info("Kafka Producer send data from Group microservice : " + groupNotificationDto);
		return groupNotificationDto;
	}

}