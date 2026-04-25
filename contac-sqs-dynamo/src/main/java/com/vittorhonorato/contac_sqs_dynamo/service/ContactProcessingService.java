package com.vittorhonorato.contac_sqs_dynamo.service;

import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;

public interface ContactProcessingService {
    void process(ContactQueueMessage message);
}
