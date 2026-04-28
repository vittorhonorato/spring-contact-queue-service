package com.vittorhonorato.contac_sqs_dynamo.service.impl;

import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;
import com.vittorhonorato.contac_sqs_dynamo.entity.ContactStatus;
import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;
import com.vittorhonorato.contac_sqs_dynamo.repository.ContactRepository;
import com.vittorhonorato.contac_sqs_dynamo.service.ContactProcessingService;
import com.vittorhonorato.contac_sqs_dynamo.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContactProcessingServiceImpl implements ContactProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ContactProcessingServiceImpl.class);
    private final ContactRepository contactRepository;
    private final MailService mailService;
    private final CacheManager cacheManager;

    public ContactProcessingServiceImpl(
            ContactRepository contactRepository,
            MailService mailService,
            CacheManager cacheManager
    ) {
        this.contactRepository = contactRepository;
        this.mailService = mailService;
        this.cacheManager = cacheManager;
    }

    @Override
    public void process(ContactQueueMessage message) {
        ContactEntity contact = contactRepository.findById(message.getContactId())
                .orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));

        if (ContactStatus.SENT.name().equals(contact.getStatus())) {
            logger.info("Contato {} já processado. Ignorando reprocessamento.", contact.getId());
            evictCaches(contact);
            return;
        }

        try {
            logger.info("Enviando e-mail para {}", contact.getEmail());
            mailService.send(contact);
            contact.setStatus(ContactStatus.SENT.name());
            contact.setSentAt(LocalDateTime.now());
            contact.setErrorMessage(null);
            contactRepository.save(contact);
            evictCaches(contact);
        } catch (Exception e) {
            logger.error("Erro ao processar contato {}", contact.getId(), e);
            contact.setStatus(ContactStatus.ERROR.name());
            contact.setSentAt(null);
            contact.setErrorMessage(normalizeErrorMessage(e.getMessage()));
            contactRepository.save(contact);
            evictCaches(contact);
            throw new RuntimeException("Falha ao enviar e-mail para contato " + contact.getId(), e);
        }
    }

    private void evictCaches(ContactEntity contact) {
        evict("contacts", contact.getId());
        if (contact.getEmail() != null) {
            evict("contactsByEmail", contact.getEmail());
        }
        evict("contactsAll", "all");
    }

    private void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private String normalizeErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            return "Erro desconhecido no envio de e-mail";
        }

        int maxLength = 255;
        if (errorMessage.length() <= maxLength) {
            return errorMessage;
        }
        return errorMessage.substring(0, maxLength);
    }
}
