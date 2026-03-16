package br.com.shipay.ms_bkd_user.infraestructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserRepository, Long> {
    boolean existsByEmail(String email);
}