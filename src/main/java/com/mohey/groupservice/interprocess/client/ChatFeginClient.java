package com.mohey.groupservice.interprocess.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mohey.groupservice.interprocess.dto.ChatCommunicationDto;

@org.springframework.cloud.openfeign.FeignClient(name = "chat-service",url = "http://${my.config.url}:8000/chats")
public interface ChatFeginClient {
	@PostMapping("/create")
	public ResponseEntity<String> create(@RequestBody ChatCommunicationDto receive);

	@PostMapping("/accept")
	public ResponseEntity<String> accept(@RequestBody ChatCommunicationDto receive);

	@PostMapping("/modify")
	public ResponseEntity<String> modify(@RequestBody ChatCommunicationDto receive);

	@PostMapping("/exit")
	public ResponseEntity<String> exit(@RequestBody ChatCommunicationDto receive);
}
