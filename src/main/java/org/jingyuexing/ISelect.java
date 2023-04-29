package org.jingyuexing;

public interface ISelect<T> {
    public String where(String ...statement);

    public String count();
}
