package com.veeteq.documentmngr.poc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DocRepository extends JpaRepository<Doc, Long> {

    @Query(value = "SELECT d FROM Doc d LEFT JOIN FETCH d.docItems di WHERE d.id = ?1")
    Optional<Doc> findByIdWithLock(Long id);
}
