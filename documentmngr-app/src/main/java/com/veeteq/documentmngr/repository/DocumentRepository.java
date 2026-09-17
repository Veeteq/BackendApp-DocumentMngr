package com.veeteq.documentmngr.repository;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    long countByAccount(Account account);

    @Query("""
    SELECT DISTINCT d.name
      FROM Document d
     WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :pattern, '%'))
    """)
    Set<String> findDistinctNames(@Param("pattern") String pattern);

    @Query("""
    SELECT DISTINCT d.comment
      FROM Expense e
     WHERE LOWER(i.comment) LIKE LOWER(CONCAT('%', :pattern, '%'))
     UNION
     SELECT DISTINCT i.comment
      FROM Income i
     WHERE LOWER(i.comment) LIKE LOWER(CONCAT('%', :pattern, '%'))
    """)
    Set<String> findDistinctComments(@Param("pattern") String pattern);
}
