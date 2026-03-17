package br.com.shipay.ms_bkd_user.application.service;

import br.com.shipay.ms_bkd_user.application.port.in.CreateUserUseCase;
import br.com.shipay.ms_bkd_user.application.port.out.RoleRepositoryPort;
import br.com.shipay.ms_bkd_user.application.port.out.UserRepositoryPort;
import br.com.shipay.ms_bkd_user.domain.exceptions.ResourceNotFoundException;
import br.com.shipay.ms_bkd_user.domain.model.RoleDomain;
import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.request.UserCreateRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final RoleRepositoryPort roleRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public CreateUserService(RoleRepositoryPort roleRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserDomain execute(UserCreateRequestDTO requestDTO) {

        RoleDomain role = roleRepositoryPort.findById(requestDTO.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        UserDomain userDomain = UserDomain.create(requestDTO.name(), requestDTO.email(), requestDTO.password(), role);

        return userRepositoryPort.save(userDomain);

    }
}
