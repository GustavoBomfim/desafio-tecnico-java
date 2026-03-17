package br.com.shipay.ms_bkd_user.application.port.out;

import br.com.shipay.ms_bkd_user.domain.model.UserDomain;

public interface UserRepositoryPort {
    UserDomain save(UserDomain userDomain);

    boolean existsByEmail(String email);
}
