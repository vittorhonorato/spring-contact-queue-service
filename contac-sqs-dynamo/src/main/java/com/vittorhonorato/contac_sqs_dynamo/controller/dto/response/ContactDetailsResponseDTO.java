package com.vittorhonorato.contac_sqs_dynamo.controller.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContactDetailsResponseDTO {
    private String id;
    private String name;
    private String email;
    private String subject;
    private String status;
    private LocalDateTime createdAt;
}
