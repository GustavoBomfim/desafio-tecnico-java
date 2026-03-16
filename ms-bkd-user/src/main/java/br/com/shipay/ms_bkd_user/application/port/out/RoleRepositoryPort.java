package br.com.shipay.ms_bkd_user.application.port.out;

import br.com.shipay.ms_bkd_user.domain.model.RoleDomain;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<RoleDomain> findById(Integer id);
}
