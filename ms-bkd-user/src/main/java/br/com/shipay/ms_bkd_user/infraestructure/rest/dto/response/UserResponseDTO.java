package br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Objeto de resposta com os dados de um usuário")
public record UserResponseDTO(
        @Schema(description = "ID único do usuário", example = "100")
        Long id,

        @Schema(description = "Nome do usuário", example = "João da Silva")
        String name,

        @Schema(description = "E-mail do usuário", example = "joao.silva@exemplo.com")
        String email,

        @Schema(description = "ID do papel (Role) do usuário", example = "1")
        Integer roleId,

        @Schema(description = "Data de criação do usuário", example = "2024-10-25")
        LocalDate createdAt
) {
}
