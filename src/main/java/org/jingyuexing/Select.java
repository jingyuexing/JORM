package org.jingyuexing;

public class Select<T> implements ISelect<T>{
    Class<?> clazz;
    Select(Class<?> clazz){
        this.clazz = clazz.getClass();
    }
    @Override
    public String where(String... statement) {
//        Utils.Where(clazz,LogicalOperator.OR);
        return "";
    }

    @Override
    public String count() {
        return null;
    }
}
