package br.com.shipay.ms_bkd_user.infraestructure.rest.mapper;

import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response.UserResponseDTO;

public class UserRestMapper {

    public static UserResponseDTO toUserResponseDTO(UserDomain userDomain) {
        return new UserResponseDTO(
                userDomain.getId(),
                userDomain.getName(),
                userDomain.getEmail(),
                userDomain.getRole().getId(),
                userDomain.getCreatedAt()
        );
    }

}
