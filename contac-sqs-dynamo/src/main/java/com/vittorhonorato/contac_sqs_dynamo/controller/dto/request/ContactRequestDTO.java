package com.vittorhonorato.contac_sqs_dynamo.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContactRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
    private String email;

    @NotBlank(message = "O assunto é obrigatório")
    @Size(max = 150, message = "O assunto deve ter no máximo 150 caracteres")
    private String subject;

    @NotBlank(message = "A mensagem é obrigatória")
    @Size(max = 2000, message = "A mensagem deve ter no máximo 2000 caracteres")
    private String message;
}
