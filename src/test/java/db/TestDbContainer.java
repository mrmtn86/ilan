package db;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoDatabase;

/**
 * Created by mac on 30/05/17.
 */
public class TestDbContainer {

    public static MongoDatabase getMemoryDb() {
        Fongo fongo = new Fongo("mongo server 1");
        return fongo.getDatabase("mydb");
    }

}
