package com.vittorhonorato.contac_sqs_dynamo.controller.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ContactResponseDTO {
    private String id;
    private String status;
    private String message;
    private LocalDateTime createdAt;
}
