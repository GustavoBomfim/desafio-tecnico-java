package br.com.shipay.ms_bkd_user.infraestructure.rest.controller.v1;

import br.com.shipay.ms_bkd_user.application.port.in.CreateUserUseCase;
import br.com.shipay.ms_bkd_user.application.port.in.GetUserByIdUseCase;
import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.request.UserCreateRequestDTO;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response.UserResponseDTO;
import br.com.shipay.ms_bkd_user.infraestructure.rest.mapper.UserRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@Tag(name = "User")
@RequestMapping("/v1/users")
@RestController
@Slf4j
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserByIdUseCase getUserByIdUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
    }

    @Operation(summary = "Criar um novo usuário", description = "Registra um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateRequestDTO requestDTO) {
        log.info("Recebendo requisição para criação de usuário");
        UserDomain userDomain = createUserUseCase.execute(requestDTO);

        UserResponseDTO responseDTO = UserRestMapper.toUserResponseDTO(userDomain);

        URI uri = URI.create("/v1/users/" + userDomain.getId());

        log.info("Usuário criado com sucesso: {}", responseDTO.id());
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico baseado no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Recebendo requisição para buscar usuário por ID: {}", id);

        UserDomain userDomainOptional = getUserByIdUseCase.findById(id);

        return ResponseEntity.ok(UserRestMapper.toUserResponseDTO(userDomainOptional));
    }
}
