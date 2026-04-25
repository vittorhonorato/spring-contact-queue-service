package com.vittorhonorato.contac_sqs_dynamo.mapper;

import com.vittorhonorato.contac_sqs_dynamo.controller.dto.request.ContactRequestDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactDetailsResponseDTO;
import com.vittorhonorato.contac_sqs_dynamo.controller.dto.response.ContactResponseDTO;
import com.vittorhonorato.contac_sqs_dynamo.entity.ContactEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactEntity toEntity(ContactRequestDTO requestDTO);
    ContactResponseDTO toDto(ContactEntity entity);
    ContactDetailsResponseDTO toDtoDetails(ContactEntity entity);

}
