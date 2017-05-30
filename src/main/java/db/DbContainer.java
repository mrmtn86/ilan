package db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * Created by mac on 30/05/17.
 */
public class DbContainer {
    public static MongoDatabase getCloudDb() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder = builder
                .connectTimeout(60000)
                .maxConnectionIdleTime(60000)
                .socketKeepAlive(true);

        MongoClientURI uri = new MongoClientURI(
                "mongodb://rwuser:rwuser@" +
                        "cluster0-shard-00-00-gzsts.mongodb.net:27017," +
                        "cluster0-shard-00-01-gzsts.mongodb.net:27017," +
                        "cluster0-shard-00-02-gzsts.mongodb.net:27017" +
                        "/ilanDB?ssl=true&authSource=admin", builder);

        MongoClient client = new MongoClient(uri);

        return client.getDatabase(uri.getDatabase());
    }


}
