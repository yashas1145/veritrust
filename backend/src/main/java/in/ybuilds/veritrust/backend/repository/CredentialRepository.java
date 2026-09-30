package in.ybuilds.veritrust.backend.repository;

import in.ybuilds.veritrust.backend.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository
        extends JpaRepository<Credential, Long> {

    Optional<Credential> findByCredentialId(String credentialId);

    boolean existsByCredentialId(String credentialId);
}