package com.vittorhonorato.contac_sqs_dynamo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.aws.sqs.listener.auto-startup=false",
		"spring.docker.compose.enabled=false"
})
class ContacSqsDynamoApplicationTests {

	@Test
	void contextLoads() {
	}

}
