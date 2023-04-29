## JORM


## usage

```java
import org.jingyuexing.Column;
import org.jingyuexing.SQLType;
import org.jingyuexing.Table;

@Table()
class User {
    @Column(type = SQLType.VARCHAR, length = 25)
    String name;
    @Column(type = SQLType.INT)
    int age;
    @Column(type = SQLType.VARCHAR,length = 245)
    String address;
}

class Test {
    public static void main(String[] args) {
        Repository<User> repo = repo.getInstance();
        repo.select().where(name=32,age=44).getOne();
    }
}
```