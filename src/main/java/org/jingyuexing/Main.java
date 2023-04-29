package org.jingyuexing;

import org.jingyuexing.Types.LogicalOperator;
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
        System.out.println(Utils.selectByArray("name","age","gender"));
        System.out.println(Utils.selectByArray());
        System.out.println(Utils.orderBy(OrderType.ASC,"name","age","gender"));
        System.out.println(Utils.selectByArray("name","age","gender"));;
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
        System.out.println(Utils.selectByList(list));
        System.out.println(Utils.selectByInstance(User.class));
        System.out.println(Utils.ignoreInSet(list,ig));
        System.out.println(Utils.ignoreInSet(Utils.selectByInstance(User.class),ig));
        System.out.println(Utils.AndSubStatement("A","B"));
        System.out.println(Utils.ORSubStatement("this is a simple text","this another simple text"));
        System.out.println(Utils.between(LogicalOperator.AND,12,33));
        System.out.println(Utils.listAppend(new ArrayList<String>(),"12","33","45","66","72","83"));
        System.out.println(Utils.join(" ",
                Utils.listAppend(
                        Utils.listAppend(
                                new ArrayList<String>(),
                                "select","*","from","tablename"
                        ),
                        Utils.listAppend(
                                new ArrayList<>(),
                                "where","id","=","12"
                        ),
                        Utils.listAppend(
                                new ArrayList<String>(),
                                "or","(",
                                "子句",
                                ")",
                                "AND",
                                "(",
                                "子句",
                                ")"
                        )
                )
        )
        );
        ArrayList[] arrayLists = new ArrayList[2];
        User user = new User();
        user.username = "Bob";
        user.address="北京什刹海";
        user.age=33;
        user.gender ="男";
        user.photo = "https://www.example/images/100x100";
        arrayLists = Utils.KeyWithValues(user);
        System.out.println(arrayLists);
        System.out.println(Utils.primaryKey(arrayLists[0]));
        System.out.println(Utils.eof(Utils.Insert(user)));
        System.out.println(Utils.eof(Utils.Update(user)));
        System.out.println(Integer.toHexString(12));
        System.out.println(Utils.limit(12,44));
        System.out.println(Utils.selfJoin("user","role","89","23","werrr","cccc","werosda","waef"));

        Repository<User> userRepo = Repository.getInstance(User.class);
        userRepo.select(user).where().count().getAll();
        System.out.println(
                userRepo
                        .select(user)
                        .where()
                        .count()
                        .ignoreColmuns("age","address")
                        .pagination(12,20)
                        .getAll()
        );
        System.out.println(
                userRepo
                        .select(user)
                        .update("age","gender")
                        .where("id=1")
                        .or("name=12")
                        .or("contry=`CN`")
                        .or("age>30")
                        .and("name<>`name`")
                        .getAll()
        );
    }
}