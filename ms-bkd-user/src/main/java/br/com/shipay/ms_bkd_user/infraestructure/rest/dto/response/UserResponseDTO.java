package br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response;

import java.time.LocalDate;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Integer roleId,
        LocalDate createdAt
) {
}
