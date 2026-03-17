package br.com.shipay.ms_bkd_user.infraestructure.persistence.mapper;

import br.com.shipay.ms_bkd_user.domain.model.RoleDomain;
import br.com.shipay.ms_bkd_user.infraestructure.persistence.entity.RoleEntity;

public class RoleMapper {

    public static RoleDomain toDomain(RoleEntity roleEntity) {
        return RoleDomain.restore(roleEntity.getId(), roleEntity.getDescription());
    }

    public static RoleEntity toEntity(RoleDomain role) {
        return new RoleEntity(
                role.getId(),
                role.getDescription()
        );
    }
}
