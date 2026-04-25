package com.vittorhonorato.contac_sqs_dynamo.producer;

import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;

public interface ContactProducer {
    void send(ContactQueueMessage message);
}
