package com.vittorhonorato.contac_sqs_dynamo.service;

import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;

public interface MailService {
    void send(ContactEntity contact);
}
