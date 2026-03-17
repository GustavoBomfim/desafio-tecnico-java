package br.com.shipay.ms_bkd_user.application.port.out;

import br.com.shipay.ms_bkd_user.domain.model.UserDomain;

import java.util.Optional;

public interface UserRepositoryPort {
    UserDomain save(UserDomain userDomain);

    boolean existsByEmail(String email);

    Optional<UserDomain> findById(Long id);
}
