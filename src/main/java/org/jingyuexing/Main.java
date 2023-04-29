package org.jingyuexing;

import org.jingyuexing.annotation.Column;
import org.jingyuexing.Types.MySQLType;
import org.jingyuexing.Types.OrderType;
import org.jingyuexing.annotation.Table;
import org.jingyuexing.utils.Utils;

import java.util.ArrayList;

@Table()
class User{
    @Column(type = MySQLType.VARCHAR,length = 255)
    public String username;
    @Column(type = MySQLType.INT)
    public int age;
    @Column(type = MySQLType.VARCHAR,length = 255)
    public String address;
    @Column(enums = {"P","S"},type = MySQLType.ENUM)
    public String gender;

    @Column(enums = {"P","S"},type = MySQLType.INTEGER)
    public String photo;
}

class JORM{
    public static void main(String[] args) throws Exception {
        System.out.println(Utils.template("--@--@--@--@--@--#","@","H",33,46,78,90));
        System.out.println(Utils.Enum(12,"A","M","P"));
        System.out.println(Utils.TypeWithLength(MySQLType.VARCHAR.getValue(), 255));
        System.out.println(Utils.select("name","age","gender"));
        System.out.println(Utils.select());
        System.out.println(Utils.orderBy(OrderType.ASC,"name","age","gender"));
        System.out.println(Utils.select("name","age","gender"));;
        ArrayList<String> list = new ArrayList<>();
        list.add("name");
        list.add("over");
        list.add("age");
        list.add("address");
        list.add("gender");
        list.add("photo");
        ArrayList<String> ig = new ArrayList<>();
        ig.add("name");
        ig.add("age");
        System.out.println(Utils.select(list));
        System.out.println(Utils.select(User.class));
        System.out.println(Utils.ignoreInSet(list,ig));
        System.out.println(Utils.ignoreInSet(Utils.select(User.class),ig));
    }
}