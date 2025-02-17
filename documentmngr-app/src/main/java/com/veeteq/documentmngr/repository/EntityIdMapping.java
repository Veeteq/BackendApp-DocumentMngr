package com.veeteq.documentmngr.repository;

public enum EntityIdMapping {
    ACCOUNT("select next value for user_seq", "user_pk", "select get_user_id from dual"),
    CATEGORY("select next value for cate_seq", "cate_pk", "select get_category_id from dual"),
    DOCUMENT("select next value for docu_seq", "docu_pk", "select get_document_id from dual"),
    INCOME("select next value for inco_seq", "inco_pk", "select get_income_id from dual"),
    ITEM("select next value for item_seq", "item_pk", "select get_item_id from dual"),
    EXPENSE("select next value for expe_seq", "expe_pk", "select get_expense_id from dual");

    private final String h2Sql;
    private final String oraSql;
    private final String callForId;

    EntityIdMapping(String h2Sql, String oraSql, String callForId) {
        this.h2Sql = h2Sql;
        this.oraSql = oraSql;
        this.callForId = callForId;
    }

    public String getH2Sql() {
        return h2Sql;
    }

    public String getOraSql() {
        return oraSql;
    }

    public String getCallForId() {
        return callForId;
    }
}
