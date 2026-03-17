package br.com.shipay.ms_bkd_user.infraestructure.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Objeto de requisição para criação de um usuário")
public record UserCreateRequestDTO (

        @Schema(description = "Nome do usuário", example = "João da Silva")
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @Schema(description = "E-mail do usuário", example = "joao.silva@exemplo.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @Schema(description = "ID do papel (Role) do usuário", example = "1")
        @NotNull(message = "O ID do papel (Role) é obrigatório")
        Integer roleId,

        @Schema(description = "Senha do usuário", example = "Shipay123!")
        String password
) {
}
