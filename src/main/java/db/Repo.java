package db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import model.ArabaModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 21/03/17.
 */
public class Repo {

    private final MongoDatabase db;

    public Repo() {

        MongoClientURI uri = new MongoClientURI(
                "mongodb://rwuser:rwuser@cluster0-shard-00-00-gzsts.mongodb.net:27017,cluster0-shard-00-01-gzsts.mongodb.net:27017,cluster0-shard-00-02-gzsts.mongodb.net:27017/ilanDB?ssl=true&authSource=admin");

        MongoClient client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase());
    }


    public List<ArabaModel> modelleriGetir(){
        MongoCollection<Document> modeller = db.getCollection("model");

        MongoCursor<Document> modelItr = modeller.find().iterator();

        List<ArabaModel> arabaModels = new ArrayList<>();

        while (modelItr.hasNext()){
            Document doc = modelItr.next();
            String ad = doc.getString("ad");
            String url = doc.getString("url");
            ArabaModel model = new ArabaModel(ad,url);
            arabaModels.add(model);
        }

        return arabaModels;

    }

}
