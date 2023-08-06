package com.mohey.groupservice.feign.client;

@org.springframework.cloud.openfeign.FeignClient(name="feign", url="", configuration = Config.class)
public interface FeignClient {
}
