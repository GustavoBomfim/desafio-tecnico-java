package br.com.shipay.ms_bkd_user.infraestructure.persistence.adapter;

import br.com.shipay.ms_bkd_user.application.port.out.UserRepositoryPort;
import br.com.shipay.ms_bkd_user.domain.model.UserDomain;
import br.com.shipay.ms_bkd_user.infraestructure.persistence.entity.UserEntity;
import br.com.shipay.ms_bkd_user.infraestructure.persistence.mapper.UserMapper;
import br.com.shipay.ms_bkd_user.infraestructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    public UserRepositoryAdapter(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDomain save(UserDomain userDomain) {

        final UserEntity user = UserMapper.toEntity(userDomain);

        final UserEntity userSaved = userRepository.save(user);

        return UserMapper.toDomain(userSaved);
    }

    @Override
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserDomain> findById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDomain);
    }
}
