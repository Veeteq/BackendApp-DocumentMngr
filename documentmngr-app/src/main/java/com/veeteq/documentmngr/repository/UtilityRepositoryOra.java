package com.veeteq.documentmngr.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@Profile({"qa", "prod"})
public class UtilityRepositoryOra implements UtilityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long getNextId(EntityIdMapping mapping) {
        var result = (BigDecimal) entityManager.createNativeQuery(mapping.getCallForId())
                .getSingleResult();
        return result.longValue();
    }
}

/*
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<Long> getNextIdList(int limit, EntityIdMapping mapping) {
        // Create a stored procedure query using the name of the function
        var query = entityManager.createStoredProcedureQuery("p_get_next_id_list")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, Class.class, ParameterMode.REF_CURSOR)
                .setParameter(1, limit)
                .setParameter(2, mapping.getConstraintName());

        // Execute the query
        query.execute();

        // Get the result list
        List<Long> ids = query.getResultList();

        return ids;
    }

    @Override
    public List<String> getDistinctComments(String searchText) {
        var sql = """
        SELECT DISTINCT e.comment FROM Expense e WHERE LOWER(e.comment) LIKE :searchText
        UNION
        SELECT DISTINCT i.comment FROM Income i WHERE LOWER(i.comment) LIKE :searchText
        ORDER BY 1""";
        List<String> results = entityManager.createQuery(sql, String.class)
                .setParameter("searchText", String.format("%s%s%s", '%', searchText, '%'))
                .getResultList();
        return results;
    }

    @Override
    @Transactional
    public List<Object[]> getDailyBalance(LocalDate date) {
        // Create a stored procedure query using the name of the function
        var query = entityManager.createStoredProcedureQuery("p_get_daily_saldo")
                .registerStoredProcedureParameter(1, LocalDate.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, Class.class, ParameterMode.REF_CURSOR)
                .setParameter(1, date);

        // Execute the query
        query.execute();

        // Get the result list
        List<Object[]> result = query.getResultList();
        return result;
    }
}
*/
