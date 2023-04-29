package org.jingyuexing.Types;

public enum EncodeType {
    UTF8("utf8");
    private final String value;
    EncodeType(String val){
        this.value =val;
    }
    String getValue(){
        return value;
    }
}
