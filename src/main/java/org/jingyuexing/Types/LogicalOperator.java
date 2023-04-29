package org.jingyuexing.Types;

public enum LogicalOperator{
    AND("and"),
    OR("or"),
    IN("in"),
    MORETHAN(">="),
    GT(">="),
    NOTEQUAL("!="),
    NE("!="),
    EQUAL("="),
    LESSTHAN("<="),
    LT("<="),
    NOT("not");
    private final String value;
    LogicalOperator(String val){
        value = val;
    }
    public String getValue() {
        return value;
    }
}