package org.jingyuexing.Types;

public enum MySQLType {
    NUMBER("number"),
    BIT("bit"),
    VARCHAR("varchar"),
    ENUM("enum"),
    CHAR("char"),
    DATE("date"),
    DATETIME("datetime"),
    DECIMAL("decimal"),
    INTEGER("integer"),
    SMALLINT("smallint"),
    BIGINT("bigint"),
    NULL("null"),
    INT("int");

    private final String value;

    MySQLType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}