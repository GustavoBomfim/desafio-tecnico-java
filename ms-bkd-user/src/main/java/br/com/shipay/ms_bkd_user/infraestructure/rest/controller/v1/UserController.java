package br.com.shipay.ms_bkd_user.infraestructure.rest.controller.v1;

import br.com.shipay.ms_bkd_user.application.port.in.CreateUserUseCase;
import br.com.shipay.ms_bkd_user.application.port.in.GetUserByIdUseCase;
import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.request.UserCreateRequestDTO;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response.UserResponseDTO;
import br.com.shipay.ms_bkd_user.infraestructure.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RequestMapping("/v1/users")
@RestController
@Slf4j
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserByIdUseCase getUserByIdUseCase){
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateRequestDTO requestDTO) {
        log.info("Recebendo requisição para criação de usuário");
        UserDomain userDomain = createUserUseCase.execute(requestDTO);

        UserResponseDTO responseDTO = UserRestMapper.toUserResponseDTO(userDomain);

        URI uri = URI.create("/v1/users/" + userDomain.getId());

        log.info("Usuário criado com sucesso: {}", responseDTO.id());
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Recebendo requisição para buscar usuário por ID: {}", id);
        
        UserDomain userDomainOptional = getUserByIdUseCase.findById(id);

        return ResponseEntity.ok(UserRestMapper.toUserResponseDTO(userDomainOptional));
    }
}
