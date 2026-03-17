package br.com.shipay.ms_bkd_user.application.port.in;

import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.request.UserCreateRequestDTO;

public interface CreateUserUseCase {
    UserDomain execute(UserCreateRequestDTO requestDTO);
}
