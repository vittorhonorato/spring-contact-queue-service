package com.vittorhonorato.contac_sqs_dynamo.service.impl;

import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;
import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;
import com.vittorhonorato.contac_sqs_dynamo.repository.ContactRepository;
import com.vittorhonorato.contac_sqs_dynamo.service.ContactProcessingService;
import com.vittorhonorato.contac_sqs_dynamo.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ContactProcessingServiceImpl implements ContactProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ContactProcessingServiceImpl.class);
    private final ContactRepository contactRepository;
    private final MailService mailService;

    public ContactProcessingServiceImpl(ContactRepository contactRepository, MailService mailService) {
        this.contactRepository = contactRepository;
        this.mailService = mailService;
    }

    @Override
    public void process(ContactQueueMessage message) {
        ContactEntity contact = contactRepository.findById(message.getContactId())
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));

        try {
            logger.info("Enviando e-mail para {}", contact.getSubject());
            mailService.send(contact);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
