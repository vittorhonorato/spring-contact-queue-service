package com.vittorhonorato.contac_sqs_dynamo.consumer.impl;

import com.vittorhonorato.contac_sqs_dynamo.consumer.ContactConsumer;
import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;
import com.vittorhonorato.contac_sqs_dynamo.service.ContactProcessingService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContactConsumerImpl implements ContactConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ContactConsumerImpl.class);
    private final ContactProcessingService contactProcessingService;

    public ContactConsumerImpl(ContactProcessingService contactProcessingService) {
        this.contactProcessingService = contactProcessingService;
    }

    @SqsListener("${app.aws.sqs.email-queue}")
    @Override
    public void listen(ContactQueueMessage message) {
        logger.info("Consumindo mensagem da fila ..... Mensagem: {}", message.getContactId());
        contactProcessingService.process(message);
    }
}
