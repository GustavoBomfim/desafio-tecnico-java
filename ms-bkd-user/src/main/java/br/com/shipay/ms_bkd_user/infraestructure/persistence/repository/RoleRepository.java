package br.com.shipay.ms_bkd_user.infraestructure.persistence.repository;

import br.com.shipay.ms_bkd_user.infraestructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
}
