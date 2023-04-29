package org.jingyuexing;

import java.util.ArrayList;

public class Repository<T> {
    T repo;
    ArrayList<String> selected;

    ArrayList<String> ignore;
    private Repository(){
    }
    public static Repository getInstance(){
        return  new Repository();
    }
    public Repository select(String ...vals){
        for (String item : vals){
            selected.add(item);
        }
        return this;
    }

    public Repository ignoreColumn(String ...cols){
        for (String col : cols){
            ignore.add(col);
        }
        return this;
    }
    public Repository where(){
        return this;
    }

    public Repository ignoreColmuns(){
        return this;
    }

    public T getOne(){
        return repo;
    }
}
