    package br.com.shipay.ms_bkd_user.infraestructure.persistence.adapter;

    import br.com.shipay.ms_bkd_user.application.port.out.RoleRepositoryPort;
    import br.com.shipay.ms_bkd_user.domain.model.RoleDomain;
    import br.com.shipay.ms_bkd_user.infraestructure.persistence.entity.RoleEntity;
    import br.com.shipay.ms_bkd_user.infraestructure.persistence.mapper.RoleMapper;
    import br.com.shipay.ms_bkd_user.infraestructure.persistence.repository.RoleRepository;
    import org.springframework.stereotype.Component;

    import java.util.Optional;

    @Component
    public class RoleRepositoryAdapter implements RoleRepositoryPort {

        private final RoleRepository roleRepository;

        public RoleRepositoryAdapter(RoleRepository roleRepository){
            this.roleRepository = roleRepository;
        }

        @Override
        public Optional<RoleDomain> findById(Integer id){
            Optional<RoleEntity> roleEntity = roleRepository.findById(id);

            return roleEntity.map(RoleMapper::toDomain);
        }
    }
