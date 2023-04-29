package org.jingyuexing.utils;

import org.jingyuexing.Types.LogicalOperator;
import org.jingyuexing.Types.MySQLType;
import org.jingyuexing.Types.OrderType;
import org.jingyuexing.annotation.Column;
import org.jingyuexing.annotation.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

public class Utils {
    private Utils(){

    }
    public static <T> String create(Class<T> clazz) throws Exception {
        // Check if the class has the Table annotation
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new Exception("Class " + clazz.getName() + " does not have the Table annotation.");
        }

        // Get the table name from the annotation
        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();
        if("".equals(tableName)){
            String[] name = clazz.getName().split("\\.");
            tableName = name[name.length-1];
        }
        if(!"".equals(table.prefix())){
            tableName = Utils.join("",table.prefix(),tableName);
        }
        StringBuilder SQL = new StringBuilder();
        SQL.append("create table ").append(tableName).append("(");
        // Set the column names on the instance using reflection
        ArrayList<String> statementCol = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                ArrayList<String> col = new ArrayList<>();
                String colName = field.getName();
                Column column = field.getAnnotation(Column.class);
                String colType = column.type().getValue();
                if(!"".equals(column.name())){
                    colName = column.name();
                }
                col.add(colName);
                if(column.type() == MySQLType.ENUM){
                    colType = Utils.Enum((Object) column.enums());
                }
                if(column.length()!=0 && column.type() != MySQLType.ENUM){
                    colType = Utils.TypeWithLength(column.type().getValue(),column.length());
                }
                col.add(colType);
                if(column.nullable()){
                    col.add("default null");
                }else{
                    col.add("not null");
                }
                if(column.autoIncrement()){
                    col.add("auto_increment");
                }
                statementCol.add(String.join(" ",col));
            }
        }
        SQL.append(Utils.join(",",statementCol));
        SQL.append(")");

        return SQL.toString();
    }

    public static String Where(Class<?> clazz, LogicalOperator op) throws Exception{
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> statement = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            strings.add(Utils.join("=", field.getName(),String.valueOf(field.get(clazz))));
        }
        statement.add("where");
        statement.add(Utils.join(Utils.join("", op.getValue()," "), strings));
        return String.join(" ", statement);
    }

    public static String between(LogicalOperator op, Object begin, Object end) {
        return Utils.join(
            " ",
            "between",
            Utils.join(
                Utils.join(
                    "",
                    " ",
                    op.getValue(),
                    " "
                ),
                String.valueOf(begin),
                String.valueOf(end)
            )
        );
    }

    public static String orderBy(OrderType order, String... cols) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("order");
        strings.add("by");
        strings.add(String.join(",", cols));
        strings.add(order.getValue());
        return String.join(" ", strings);
    }

    public static String Like(Object ... statement) {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> metals = new ArrayList<>();
        for (Object item:statement) {
            metals.add(String.valueOf(item));
        }
        String innerStatement = String.join("%", metals);
        strings.add("like");
        strings.add("'"+innerStatement+"'");
        return String.join(" ", strings);
    }

    public static String select(String ...cols){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("select");
        strings.add(Utils.ColumnsJoin(cols));
        strings.add("from");
        return String.join(" ",strings);
    }
    public static String select(ArrayList<String> cols){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("select");
        strings.add(Utils.ColumnsJoin(cols));
        strings.add("from");
        return String.join(" ",strings);
    }
    public static ArrayList<String> select(Class<?> clazz){
        Class<?> class_ = clazz.getClass();
        ArrayList<String> selectedCols = new ArrayList<>();
        for (Field field:clazz.getDeclaredFields()){
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
            strings.add(String.join(",", cols));
            strings.add(")");
        }
        return Utils.join("",strings);
    }

    public static String alter(String tablename) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("alter");
        strings.add("table");
        strings.add(tablename);
        return Utils.join(" ", strings);
    }

    public static String update(Class<?> clazz, String... where) throws Exception {
        ArrayList<String> strings = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // Ensure that the field can be accessed
            String[] col = new String[] { field.getName(), String.valueOf(field.get(clazz)) }; // Use reflection to get
                                                                                               // the field value
            strings.add(String.join("=", col));
        }
        return Utils.join(" ", strings);
    }

    public static String template(String template,String delimiter, Object ...values){
        String tempCopy = template;
        for (Object item:values) {

            tempCopy = tempCopy.replaceFirst("["+delimiter+"]",String.valueOf(item));
        }
        return  tempCopy;
    }
    public static String Enum(Object ...vals){
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> Evals = new ArrayList<>();
        strings.add("enum");
        strings.add("(");
        for (Object val : vals) {
            Evals.add(String.valueOf(val));
        }
        strings.add(Utils.join(",",Evals));
        strings.add(")");
        return Utils.join("",strings);
    }
    public static String TypeWithLength(String type,int length){
        return Utils.join(
            " ",
            type,
            "(",
            String.valueOf(length),
            ")"
        );
    }

    public static  ArrayList<String> ignoreInSet(ArrayList<String> set,ArrayList<String> ignores){
        ArrayList<String> final_ = new ArrayList<>();
        for (String item:set) {
            if(!ignores.contains(item)){
                final_.add(item);
            }
        }
        return  final_;
    }
    public static String AndSubStatement(String prev,String next){
        return Utils.SubStatement(prev, next, LogicalOperator.AND);
    }
    public static String ORSubStatement(String prev, String next){
        return Utils.SubStatement(prev, next, LogicalOperator.OR);
    }
    public static String InSubStatement(String prev, String next){
        return Utils.SubStatement(prev, next, LogicalOperator.IN);
    }
    public static String join(String delimiter,String ...vals){
        return String.join(delimiter, vals);
    }
    public static String join(String delimiter,ArrayList<String> vals){
        return String.join(delimiter, vals);
    }
    public static String SubStatement(String prev, String next,LogicalOperator op){
        return Utils.join(
            Utils.join(
                "",
                " ",
                op.getValue(),
                " "
            ),
            Utils.join(" ",
                "(",
                prev,
                ")"
            ),
            Utils.join(
                " ",
                "(",
                next,
                ")"
            )
        );
    }

    public static <T> T map2Class(Map<String, Object> map, Class<T> clazz) throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                field.set(instance, map.get(field.getName()));
            }
        }
        return instance;
    }
}