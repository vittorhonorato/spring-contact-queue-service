package com.vittorhonorato.contac_sqs_dynamo.repository;

import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;

import java.util.List;
import java.util.Optional;

public interface ContactRepository {
    void save(ContactEntity entity);
    Optional<ContactEntity> findById(String id);
    Optional<ContactEntity> findByEmail(String email);
    List<ContactEntity> findAll();
}
