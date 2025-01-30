package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findByNameContainingIgnoreCase(String pattern, Pageable pageable);

    List<Account> findByNameStartsWithIgnoreCase(String patter);
}
