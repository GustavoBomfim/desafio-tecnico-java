package br.com.shipay.ms_bkd_user.application.service;

import br.com.shipay.ms_bkd_user.application.port.in.GetUserByIdUseCase;
import br.com.shipay.ms_bkd_user.application.port.out.UserRepositoryPort;
import br.com.shipay.ms_bkd_user.domain.exceptions.ResourceNotFoundException;
import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public GetUserByIdService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserDomain findById(Long id) {

         return userRepositoryPort.findById(id).orElseThrow(() -> {
             log.warn("Usuário não encontrado com ID: {}", id);
             return new ResourceNotFoundException("User not found");
         });

    }
}
