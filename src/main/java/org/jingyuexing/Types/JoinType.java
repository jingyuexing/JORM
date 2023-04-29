package org.jingyuexing.Types;

public enum JoinType {
    CROSS("cross"),
    LEFT("left"),
    RIGHT("right"),
    FULL("full"),
    SELF("self"),
    INNER("inner");
    private final String value;
    JoinType(String val){
        value = val;
    }
    public String getValue(){
        return this.value;
    }
}
