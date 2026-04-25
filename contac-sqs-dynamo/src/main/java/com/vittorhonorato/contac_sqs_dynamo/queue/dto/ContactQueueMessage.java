package com.vittorhonorato.contac_sqs_dynamo.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactQueueMessage {
    private String contactId;
}
