package br.com.shipay.ms_bkd_user.infraestructure.rest.controller;

import br.com.shipay.ms_bkd_user.application.port.in.CreateUserUseCase;
import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.request.UserCreateRequestDTO;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response.UserResponseDTO;
import br.com.shipay.ms_bkd_user.infraestructure.rest.mapper.UserRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/v1/users")
@RestController
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase){
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO requestDTO) {
        UserDomain userDomain = createUserUseCase.execute(requestDTO);

        UserResponseDTO responseDTO = UserRestMapper.toUserResponseDTO(userDomain);

        URI uri = URI.create("/v1/users/" + userDomain.getId());

        return ResponseEntity.created(uri).body(responseDTO);
    }
}
