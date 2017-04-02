package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import entity.ArabaModel;
import model.ArabaIlan;
import model.ModelinIlanlari;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public List<ArabaModel> modelleriGetir() {
        MongoCollection<Document> modeller = db.getCollection("model");

        MongoCursor<Document> modelItr = modeller.find().iterator();

        List<ArabaModel> arabaModels = new ArrayList<>();

        while (modelItr.hasNext()) {
            Document doc = modelItr.next();
            String ad = doc.getString("ad");
            String url = doc.getString("url");
            ObjectId id = doc.getObjectId("_id");
            ArabaModel model = new ArabaModel(ad, url , id);

            arabaModels.add(model);
        }
        return arabaModels;
    }

    public ArabaIlan IlaniKaydet(ArabaIlan arabaIlan) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(arabaIlan);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        MongoCollection<Document> modeller = db.getCollection("ilan");
        modeller.insertOne(Document.parse(json));

        return arabaIlan;
    }


    public void ilanGuncelle(ArabaIlan ilanDb) {

    }


    public Map<Integer, ArabaIlan> modelinKayitlariniGetir(ObjectId modelId, int yilParam) {
        MongoCollection<Document> modeller = db.getCollection("ilan");

        MongoCursor<Document> modelItr = modeller.find(new Document("modelId",modelId)).iterator();

        Map<Integer, ArabaIlan> integerArabaIlanMap = new HashMap<>();

        while (modelItr.hasNext()) {
            Document doc = modelItr.next();


            int km = doc.getInteger("km");
            int fiyat = doc.getInteger("fiyat");
            String ilanUrl = doc.getString("url");
            String tarihStr = doc.getString("tarih");
            String baslik = doc.getString("baslik");
            int ilanNoInt = doc.getInteger("ilanNo");


            ArabaIlan arabaIlan = new ArabaIlan(yilParam, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt);

            integerArabaIlanMap.put(arabaIlan.ilanNo, arabaIlan);
        }
        return integerArabaIlanMap;
    }

}
