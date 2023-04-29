package org.jingyuexing.utils;

import org.jingyuexing.Types.LogicalOperator;
import org.jingyuexing.Types.MySQLType;
import org.jingyuexing.Types.OrderType;
import org.jingyuexing.annotation.Column;
import org.jingyuexing.annotation.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Utils {
    public static String space = " ";
    public static String line = "|";
    private Utils() {

    }

    public static <T> String create(Class<T> clazz) throws Exception {
        // Check if the class has the Table annotation
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new Exception("Class " + clazz.getName() + " does not have the Table annotation.");
        }

        // Get the table name from the annotation
        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();
        if ("".equals(tableName)) {
            String[] name = clazz.getName().split("\\.");
            tableName = name[name.length - 1].toLowerCase();
        }
        if (!"".equals(table.prefix())) {
            tableName = Utils.join("", table.prefix(), tableName);
        }
        ArrayList<String> SQL = new ArrayList<>();
        Utils.listAppend(SQL, Utils.createTable(tableName), "(");

        ArrayList<String> uniqueList = new ArrayList<>();
        // Set the column names on the instance using reflection
        ArrayList<String> statementCol = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                ArrayList<String> col = new ArrayList<>();
                String colName = field.getName();
                Column column = field.getAnnotation(Column.class);
                String colType = column.type().getValue();
                if (!"".equals(column.name())) {
                    colName = column.name();
                }
                col.add(colName);
                if (column.type() == MySQLType.ENUM) {
                    colType = Utils.Enum((Object) column.enums());
                }
                if (column.length() != 0 && column.type() != MySQLType.ENUM) {
                    colType = Utils.TypeWithLength(column.type().getValue(), column.length());
                }
                Utils.listAppend(col, colType);
                if (column.nullable()) {
                    Utils.listAppend(col, "default", "null");
                } else {
                    Utils.listAppend(col, "not", "null");
                }

                if(column.unique()){
                    uniqueList.add(field.getName());
                }

                if (column.autoIncrement()) {
                    Utils.listAppend(col, "auto_increment");
                }
                statementCol.add(Utils.join(Utils.space, col));
            }
        }
        statementCol.add(Utils.constraint(Utils.join("_", "UC",tableName), uniqueList));
        Utils.listAppend(SQL, Utils.join(",", statementCol));
        
        Utils.listAppend(SQL, ")");
        return Utils.join(Utils.space, SQL);
    }

    public static String Where(Class<?> clazz, LogicalOperator op) throws Exception {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> statement = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Utils.listAppend(
                    strings,
                    Utils.join(
                            "=",
                            field.getName(),
                            field.get(clazz).toString()));
        }
        statement.add("where");
        statement.add(Utils.join(Utils.join("", op.getValue(), Utils.space), strings));
        return Utils.join(Utils.space, statement);
    }

    public static <T> ArrayList<String>[] KeyWithValues(T entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Utils.listAppend(keys, field.getName());
            Utils.listAppend(values, String.valueOf(field.get(entity)));
        }
        ArrayList<String>[] keyWithValue = new ArrayList[] { keys, values };
        return keyWithValue;
    }

    public static <T> String Insert(T entity) throws IllegalAccessException {
        ArrayList[] keyWithValue = Utils.KeyWithValues(entity);
        ArrayList<String> keys = keyWithValue[0];
        ArrayList<String> values = keyWithValue[1];
        ArrayList<String> statement = new ArrayList<>();
        Utils.listAppend(statement, "insert", "into");
        Utils.listAppend(statement,
                Utils.join(Utils.space, "(", Utils.join(",", keys), ")"));
        Utils.listAppend(statement, "values", "(");
        Utils.listAppend(statement, join(",", values), ")");
        return Utils.join(Utils.space, statement);

    }

    public static <T> String subStatementSet(T entity) throws IllegalAccessException {
        ArrayList[] keyWithValue = Utils.KeyWithValues(entity);
        ArrayList<String> keys = keyWithValue[0];
        ArrayList<String> values = keyWithValue[1];
        ArrayList<String> setStatement = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            Utils.listAppend(setStatement, Utils.join("=", keys.get(i), values.get(i)));
        }
        return Utils.join(Utils.space, "set", Utils.join(", ", setStatement));

    }

    public static <T> String Update(T entity) throws IllegalAccessException {
        ArrayList<String> updateStatement = new ArrayList<>();
        Utils.listAppend(updateStatement, "update", "tablename", Utils.subStatementSet(entity));
        return Utils.join(Utils.space, updateStatement);
    }

    public static String eof(String statement) {
        return Utils.join(Utils.space, statement, ";");
    }

    public static String between(LogicalOperator op, Object begin, Object end) {
        return Utils.join(
                Utils.space,
                "between",
                Utils.join(
                        Utils.join(
                                "",
                                " ",
                                op.getValue(),
                                " "),
                        String.valueOf(begin),
                        String.valueOf(end)));
    }

    public static String orderBy(OrderType order, String... cols) {
        return Utils.join(Utils.space, Utils.orderBy(cols), order.getValue());
    }

    public static String Delete(String table){
        return Utils.join(Utils.space, "delete","from",table);
    }

    public static String orderBy(String... cols) {
        return Utils.join(Utils.space, "order", "by", Utils.join(",", cols));
    }

    public static String Like(Object... statement) {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> metals = new ArrayList<>();
        for (Object item : statement) {
            metals.add(String.valueOf(item));
        }
        String innerStatement = Utils.join("%", metals);
        strings.add("like");
        strings.add(Utils.join("", "'", innerStatement, "'"));
        return String.join(Utils.space, strings);
    }

    public static String select(String... cols) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("select");
        strings.add(Utils.ColumnsJoin(cols));
        strings.add("from");
        return String.join(Utils.space, strings);
    }

    public static String select(ArrayList<String> cols) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("select");
        strings.add(Utils.ColumnsJoin(cols));
        strings.add("from");
        return String.join(Utils.space, strings);
    }

    public static ArrayList<String> select(Class<?> clazz) {
        Class<?> class_ = clazz;
        ArrayList<String> selectedCols = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            selectedCols.add(field.getName());
        }
        return selectedCols;
    }

    public static String ColumnsJoin(String... cols) {
        ArrayList<String> strings = new ArrayList<>();
        if (cols.length == 0) {
            strings.add("*");
        } else {
            strings.add("(");
            strings.add(String.join(",", cols));
            strings.add(")");
        }
        return String.join("", strings);
    }

    public static String ColumnsJoin(ArrayList<String> cols) {
        ArrayList<String> strings = new ArrayList<>();
        if (cols.size() == 0) {
            strings.add("*");
        } else {
            strings.add("(");
            strings.add(Utils.join(",", cols));
            strings.add(")");
        }

        return Utils.join("", strings);
    }

    public static String alter(String tablename) {
        return Utils.join(Utils.space, "alter", "table", tablename);
    }

    public static String update(Class<?> clazz, String... where) throws Exception {
        ArrayList<String> strings = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // Ensure that the field can be accessed
            String[] col = new String[] { field.getName(), String.valueOf(field.get(clazz)) }; // Use reflection to get
                                                                                               // the field value
            strings.add(String.join("=", col));
        }
        return Utils.join(Utils.space, strings);
    }

    public static String template(String template, String delimiter, Object... values) {
        String tempCopy = template;
        for (Object item : values) {
            tempCopy = tempCopy.replaceFirst("[" + delimiter + "]", String.valueOf(item));
        }
        return tempCopy;
    }

    public static String limit(int limit) {
        return Utils.join(Utils.space, "limit", String.valueOf(limit));
    }

    public static String limit(int limit, int offset) {
        return Utils.join(Utils.space, "limit", Utils.join(",", String.valueOf(offset), String.valueOf(limit)));
    }

    public static String max(String... columns) {
        return Utils.buildInMethod("max", columns);
    }

    public static String min(String... columns) {
        return Utils.buildInMethod("min", columns);
    }

    public static String avg(String... columns) {
        return Utils.buildInMethod("avg", columns);
    }

    public static String sum(String... columns) {
        return Utils.buildInMethod("sum", columns);
    }

    public static String count(String... columns) {
        return Utils.buildInMethod("count", columns);
    }

    public static String buildInMethod(String name, String... columns) {
        if (columns.length != 0) {
            return Utils.join("", name, "(", Utils.join(",", columns), ")");
        } else {
            return Utils.join("", name, "(", "*", ")");
        }
    }

    public static String unique(String ...columns){
        return Utils.join(Utils.space, "unique","(",Utils.join(",", columns),")");
    }


    public static String unique(ArrayList<String> columns){
        return Utils.join(Utils.space, "unique","(",Utils.join(",", columns),")");
    }
    
    public static String constraint(String name,String ...cols){
        return Utils.join(Utils.space, "constraint",Utils.unique(cols));
    }
    public static String constraint(String name,ArrayList<String> cols){
        return Utils.join(Utils.space, "constraint",Utils.unique(cols));
    }

    public static String crossJoin(String... tables) {
        return Utils.join(" cross join ", tables);
    }

    public static String leftJoin(String... tables) {
        return Utils.join(" left join ", tables);
    }

    public static String createTable(String name){
        return Utils.create(name,"table");
    }
    public static String createDatabase(String name){
        return Utils.create(name,"database");
    }

    public static String create(String name,String type){
        return Utils.join(Utils.space, "create",type,name);
    }
    public static String rightJoin(String... tables) {
        return Utils.join(" right join ", tables);
    }

    public static String innerJoin(String... tables) {
        return Utils.join(" inner join ", tables);
    }

    public static String JoinOn(String... expression) {
        return Utils.join(Utils.space, "on", Utils.join("=", expression));
    }

    public static String offset(int offset) {
        return Utils.join(Utils.space, "offset", String.valueOf(offset));
    }

    public static String Enum(Object... vals) {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> Evals = new ArrayList<>();
        strings.add("enum");
        strings.add("(");
        for (Object val : vals) {
            Evals.add(String.valueOf(val));
        }
        strings.add(Utils.join(",", Evals));
        strings.add(")");
        return Utils.join("", strings);
    }

    public static String TypeWithLength(String type, int length) {
        return Utils.join(
                Utils.space,
                type,
                "(",
                String.valueOf(length),
                ")");
    }

    public static ArrayList<String> ignoreInSet(ArrayList<String> set, ArrayList<String> ignores) {
        ArrayList<String> final_ = new ArrayList<>();
        for (String item : set) {
            if (!ignores.contains(item)) {
                final_.add(item);
            }
        }
        return final_;
    }

    public static String AndSubStatement(String prev, String next) {
        return Utils.SubStatement(prev, next, LogicalOperator.AND);
    }

    public static String ORSubStatement(String prev, String next) {
        return Utils.SubStatement(prev, next, LogicalOperator.OR);
    }

    public static String InSubStatement(String prev, String next) {
        return Utils.SubStatement(prev, next, LogicalOperator.IN);
    }

    public static String join(String delimiter, String... vals) {
        return String.join(delimiter, vals);
    }

    public static String join(String delimiter, ArrayList<String> vals) {
        return String.join(delimiter, vals);
    }

    public static String SubStatement(String prev, String next, LogicalOperator op) {
        return Utils.join(
                Utils.join(
                        "",
                        " ",
                        op.getValue(),
                        " "),
                Utils.join(Utils.space,
                        "(",
                        prev,
                        ")"),
                Utils.join(
                        Utils.space,
                        "(",
                        next,
                        ")"));
    }

    public static <T> T map2Class(Map<String, Object> map, Class<T> clazz)
            throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                field.set(instance, map.get(field.getName()));
            }
        }
        return instance;
    }

    public static <E> ArrayList<E> listAppend(ArrayList<E> target, E... vals) {
        Collections.addAll(target, vals);
        return target;
    }

    public static <E> ArrayList<E> listAppend(ArrayList<E> target, ArrayList<E> source) {
        target.addAll(source);
        return target;
    }

    public static <E> ArrayList<E> listAppend(ArrayList<E> target, ArrayList<E>... source) {
        for (ArrayList<E> list : source) {
            Utils.listAppend(target, list);
        }
        return target;
    }
}