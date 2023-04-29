package org.jingyuexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jingyuexing.Types.LogicalOperator;
import org.jingyuexing.utils.Utils;

public class Repository<T> {
    T entity;
    Class<T> entityClass;
    Map<String, ArrayList<String>> subStatement = new HashMap<>();

    private Repository() {
    }

    public static <T> Repository<T> getInstance(T entity) {
        Repository<T> repo = new Repository<T>();
        repo.entity = entity;
        return repo;
    }

    public static <T> Repository<T> getInstance(Class<T> entity) {
        Repository<T> repo = new Repository<T>();
        repo.entityClass = entity;
        return repo;
    }

    public Repository<T> select(String... vals) {
        ArrayList<String> list = new ArrayList<>();
        for (String item : vals) {
            list.add(item);
        }
        subStatement.put("selected", list);
        return this;
    }

    public Repository<T> update(String... cols) throws Exception {
        ArrayList<String> statement = new ArrayList<>();
        if (cols.length == 0) {
            subStatement.put(
                    "update",
                    Utils.listAppend(
                            statement,
                            Utils.Update(this.entity)));
        } else {
            ArrayList<String>[] keyValuePair = Utils.KeyWithValues(this.entity);
            ArrayList<String> values = new ArrayList<>();
            for (String key : cols) {
                if (keyValuePair[0].contains(key)) {
                    int index = keyValuePair[0].indexOf(key);
                    Utils.listAppend(
                        values,
                        Utils.join("=", key, keyValuePair[1].get(index)));
                }

            }
            Utils.listAppend(statement, "update",getTableName(),"set",Utils.join(",", values));
           this.subStatement.put("update",statement);
        }
        return this;
    }

    public Repository<T> ignoreColumn(String... cols) {
        ArrayList<String> ignore = new ArrayList<>();
        for (String col : cols) {
            ignore.add(col);
        }
        subStatement.put("ignore", ignore);
        return this;
    }

    public Repository<T> where(String ...expression) {
        ArrayList<String> list = new ArrayList<>();
        subStatement.put(
                "where",
                Utils.listAppend(list,expression)
                );
        return this;
    }

    public Repository<T> select(T entity) {
        this.entity = entity;
        if (!subStatement.containsKey("select")) {
            subStatement.put(
                    "select",
                    Utils.selectByInstance(entity));
        }
        return this;
    }

    public Repository<T> limit(int limit) {
        if (!this.subStatement.containsKey("limit")) {
            this.subStatement.put(
                    "limit",
                    Utils.listAppend(
                            new ArrayList<String>(),
                            Utils.limit(limit)));
        }
        return this;
    }

    public Repository<T> limit(int limit, int offset) {
        if (!this.subStatement.containsKey("limit")) {
            this.subStatement.put(
                    "limit",
                    Utils.listAppend(new ArrayList<String>(),
                            Utils.limit(
                                    limit,
                                    offset)));
        }
        return this;
    }

    public Repository<T> offset(int offset) {
        subStatement.put(
                "offset",
                Utils.listAppend(
                        new ArrayList<String>(),
                        Utils.offset(offset)));
        return this;
    }

    public Repository<T> pagination(int page, int pagesize) {
        return this.limit(pagesize).offset(page * pagesize);
    }

    public Repository<T> count() {
        Utils.ColumnsJoin(
                Utils.selectByInstance(entity));
        return this;
    }

    public String getAll() {
        // ArrayList<T> result = new ArrayList<>();
        return this.builder();
    }

    public Repository<T> or(String ...expression) {
        String prev = Utils.join(" ",subStatement.get("where"));
        String next = Utils.join(Utils.space,Utils.join(Utils.space,expression));
        subStatement.put("where",Utils.listAppend(new ArrayList<String>(),Utils.ORSubStatement(prev,next)));
        return this;
    }

    public Repository<T> and(String expression) {
        String prev = Utils.join(" ",subStatement.get("where"));
        String next = Utils.join(Utils.space,Utils.join(Utils.space,expression));
        subStatement.put("where",Utils.listAppend(new ArrayList<>(),Utils.AndSubStatement(prev,next)));
        return this;
    }

    public Repository<T> ignoreColmuns(String... columns) {
        if (subStatement.containsKey("select")) {
            for (String item : columns) {
                subStatement.get("select").remove(subStatement.get("select").indexOf(item));
            }
        }
        return this;
    }

    private String getTableName() {
        if (entity != null) {
            String[] name = entity.getClass().getName().split("\\.");
            return name[name.length - 1].toLowerCase();
        } else {
            String[] name = entityClass.getClass().getName().split("\\.");
            return name[name.length - 1].toLowerCase();
        }

    }

    // public T getOne(){
    // this.builder();
    // return repo;
    // }
    private String builder() {
        ArrayList<String> expression = new ArrayList<>();
        if (subStatement.containsKey("select")) {
            Utils.listAppend(
                    expression,
                    Utils.selectByList(
                            subStatement.get("select")));
            Utils.listAppend(
                    expression,
                    this.getTableName());
        }
        if (subStatement.containsKey("where")) {
            Utils.listAppend(
                    expression,
                    subStatement.get("where"));
        }
        if (subStatement.containsKey("limit")) {
            if (subStatement.containsKey("offset")) {
                Utils.listAppend(
                        expression,
                        subStatement.get("limit"),
                        subStatement.get("offset"));
            } else {
                Utils.listAppend(
                        expression,
                        subStatement.get("limit"));
            }
        }
        if (subStatement.containsKey("update")){
            if(subStatement.containsKey("where")){
                return Utils.join(
                    Utils.space,
                    Utils.eof(Utils.listAppend(
                            subStatement.get("update"),
                            "where",
                            Utils.join(" ", subStatement.get("where"))
                    ))
                );
            }
            return Utils.join(Utils.space, subStatement.get("update"));
        }
        this.subStatement = new HashMap<>();
        return Utils.join(Utils.space, Utils.eof(expression));
    }
}
