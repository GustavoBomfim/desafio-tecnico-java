package br.com.shipay.ms_bkd_user.infraestructure.persistence.mapper;

import br.com.shipay.ms_bkd_user.domain.model.RoleDomain;
import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.persistence.entity.RoleEntity;
import br.com.shipay.ms_bkd_user.infraestructure.persistence.entity.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(UserDomain userDomain){

        final RoleEntity roleEntity = RoleMapper.toEntity(userDomain.getRole());


        return new UserEntity(
                userDomain.getName(),
                userDomain.getEmail(),
                userDomain.getPassword(),
                roleEntity
        );
    }

    public static UserDomain toDomain(UserEntity userSaved) {

        RoleDomain role = RoleMapper.toDomain(userSaved.getRole());

        return UserDomain.restore(
                userSaved.getId(),
                userSaved.getName(),
                userSaved.getEmail(),
                userSaved.getPassword(),
                role,
                null,
                userSaved.getCreatedAt(),
                userSaved.getUpdatedAt()
        );
    }
}
