package com.vittorhonorato.contac_sqs_dynamo.consumer;

import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;

public interface ContactConsumer {
    void listen(ContactQueueMessage message);
}
