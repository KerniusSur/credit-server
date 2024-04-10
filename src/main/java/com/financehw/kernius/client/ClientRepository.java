package com.financehw.kernius.client;

import com.financehw.kernius.client.entity.ClientEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
  Optional<ClientEntity> findByPersonalId(String personalId);

  Optional<ClientEntity> findByEmail(String email);

  Boolean existsByEmail(String email);

  Boolean existsByPersonalId(String personalId);
}
