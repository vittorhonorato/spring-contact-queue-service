package com.vittorhonorato.contac_sqs_dynamo.repository.impl;

import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;
import com.vittorhonorato.contac_sqs_dynamo.repository.ContactRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class ContactRepositoryImpl implements ContactRepository {

    private final DynamoDbTable<ContactEntity> contactTable;

    public ContactRepositoryImpl(DynamoDbTable<ContactEntity> contactTable) {
        this.contactTable = contactTable;
    }

    @Override
    public void save(ContactEntity entity) {
        contactTable.putItem(entity);
    }

    @Override
    public Optional<ContactEntity> findById(String id) {
        ContactEntity entity = contactTable.getItem(
                Key.builder()
                        .partitionValue(id)
                        .build()
        );
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<ContactEntity> findByEmail(String email) {
        return StreamSupport.stream(contactTable.scan().items().spliterator(), false)
                .filter(contact -> email.equals(contact.getEmail()))
                .findFirst();
    }

    @Override
    public List<ContactEntity> findAll() {
        return StreamSupport.stream(contactTable.scan().items().spliterator(), false)
                .toList();
    }
}
