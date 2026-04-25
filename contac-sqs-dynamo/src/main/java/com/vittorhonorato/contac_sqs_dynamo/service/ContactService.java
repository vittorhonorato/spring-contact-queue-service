package com.vittorhonorato.contac_sqs_dynamo.service;

import com.vittorhonorato.contac_sqs_dynamo.controller.dto.request.ContactRequestDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactDetailsResponseDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactResponseDTO;

import java.util.List;

public interface ContactService {
    ContactResponseDTO create(ContactRequestDTO contactRequestDTO);
    ContactDetailsResponseDTO findById(String id);
    ContactDetailsResponseDTO findByEmail(String id);
    List<ContactDetailsResponseDTO> findAll();
}
