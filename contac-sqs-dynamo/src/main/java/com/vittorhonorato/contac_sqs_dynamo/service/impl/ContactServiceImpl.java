package com.vittorhonorato.contac_sqs_dynamo.service.impl;

import com.vittorhonorato.contac_sqs_dynamo.controller.dto.request.ContactRequestDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactDetailsResponseDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactResponseDTO;
import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;
import com.vittorhonorato.contac_sqs_dynamo.entity.ContactStatus;
import com.vittorhonorato.contac_sqs_dynamo.mapper.ContactMapper;
import com.vittorhonorato.contac_sqs_dynamo.producer.ContactProducer;
import com.vittorhonorato.contac_sqs_dynamo.queue.dto.ContactQueueMessage;
import com.vittorhonorato.contac_sqs_dynamo.repository.ContactRepository;
import com.vittorhonorato.contac_sqs_dynamo.service.ContactService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactProducer contactProducer;
    private final ContactMapper contactMapper;

    public ContactServiceImpl(ContactRepository contactRepository, ContactProducer contactProducer, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactProducer = contactProducer;
        this.contactMapper = contactMapper;
    }

    @CacheEvict(value = "contactsAll", key = "'all'")
    @Override
    public ContactResponseDTO create(ContactRequestDTO contactRequestDTO) {

        ContactEntity entity = contactMapper.toEntity(contactRequestDTO);
        entity.setId(UUID.randomUUID().toString());
        entity.setStatus(ContactStatus.PENDING.name());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setSentAt(null);
        entity.setErrorMessage(null);
        contactRepository.save(entity);

        contactProducer.send(new ContactQueueMessage(entity.getId()));

        ContactResponseDTO response = contactMapper.toDto(entity);
        return response;
    }

    @Cacheable(value = "contacts", key = "#id")
    @Override
    public ContactDetailsResponseDTO findById(String id) {

        ContactEntity entity = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato nao encontrado"));

        return contactMapper.toDtoDetails(entity);
    }

    @Cacheable(value = "contactsByEmail", key = "#email")
    @Override
    public ContactDetailsResponseDTO findByEmail(String email) {
        ContactEntity entity = contactRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Contato nao encontrado"));

        return contactMapper.toDtoDetails(entity);
    }

    @Cacheable(value = "contactsAll", key = "'all'")
    @Override
    public List<ContactDetailsResponseDTO> findAll() {
        return contactRepository.findAll()
                .stream()
                .map(contactMapper::toDtoDetails)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
