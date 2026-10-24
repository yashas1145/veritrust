package in.ybuilds.veritrust.backend.repository;

import in.ybuilds.veritrust.backend.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import in.ybuilds.veritrust.backend.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository
        extends JpaRepository<Institution, Long> {

    Optional<Institution> findByInstitutionCode(String institutionCode);

    Optional<Institution> findByUserId(Long userId);

    boolean existsByInstitutionCode(String institutionCode);

    List<Institution> findByVerifiedFalse();
}