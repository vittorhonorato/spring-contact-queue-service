package com.vittorhonorato.contac_sqs_dynamo.producer;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailProducer {

    private static final Logger logger = LoggerFactory.getLogger(EmailProducer.class);

    private final SqsTemplate sqsTemplate;

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String emailQeue;

    public EmailProducer(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void send(String message) {
        logger.info("Enviando mensagem {} para processamento ...", message);

        sqsTemplate.send(emailQeue, message);
    }
}
