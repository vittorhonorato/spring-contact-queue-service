package com.vittorhonorato.contac_sqs_dynamo.producer.impl;

import com.vittorhonorato.contac_sqs_dynamo.producer.ContactProducer;
import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContactProducerImpl implements ContactProducer {

    private static final Logger logger = LoggerFactory.getLogger(ContactProducerImpl.class);

    private final SqsTemplate sqsTemplate;

    @Value("${app.aws.sqs.email-queue}")
    private String emailQeue;

    public ContactProducerImpl(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    @Override
    public void send(ContactQueueMessage message) {
        logger.info("Enviando mensagem {} para processamento ...", message.getContactId());

        sqsTemplate.send(emailQeue, message);
    }
}
