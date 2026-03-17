package br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response;

public record ExcecaoResponseDTO (
        String mensagem,
        int status

) {
}
