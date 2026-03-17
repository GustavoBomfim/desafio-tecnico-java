package br.com.shipay.ms_bkd_user.application.port.in;

import br.com.shipay.ms_bkd_user.domain.model.UserDomain;

public interface GetUserByIdUseCase {
    UserDomain findById(Long id);
}
