package db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import entity.ArabaModel;
import model.ArabaIlan;
import model.IlanDurum;
import model.KullanimDurumu;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static parser.json.JsonParser.toJson;

/**
 * Created by mac on 21/03/17.
 */
public class Repo {

    private final MongoDatabase db;

    public Repo() {

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);



        MongoClientOptions asdas = MongoClientOptions.builder().connectTimeout(6000000).socketKeepAlive(true).build();

        MongoClientURI uri = new MongoClientURI(
                "mongodb://rwuser:rwuser@cluster0-shard-00-00-gzsts.mongodb.net:27017,cluster0-shard-00-01-gzsts.mongodb.net:27017,cluster0-shard-00-02-gzsts.mongodb.net:27017/ilanDB?ssl=true&authSource=admin");


        MongoClient client = new MongoClient(uri);

        db = client.getDatabase(uri.getDatabase());

    }


    public List<ArabaModel> modelleriGetir() {
        MongoCollection<Document> modeller = db.getCollection("model");

        Bson query = new Document("kullanimDurumu", KullanimDurumu.Aktif.getIndex());
        MongoCursor<Document> modelItr = modeller.find(query).iterator();

        List<ArabaModel> arabaModels = new ArrayList<>();

        while (modelItr.hasNext()) {
            Document doc = modelItr.next();
            String ad = doc.getString("ad");
            String url = doc.getString("url");
            ObjectId id = doc.getObjectId("_id");
            Object paketObj = doc.get("paketler");

            ArabaModel model = new ArabaModel(ad, url, id);

            if (paketObj != null)
                model.paketler = (List<String>) paketObj;

            arabaModels.add(model);
        }
        modelItr.close();
        return arabaModels;
    }

    public ArabaIlan IlaniKaydet(ArabaIlan arabaIlan) {
        String json = toJson(arabaIlan);

        MongoCollection<Document> modeller = getIlan();
        modeller.insertOne(Document.parse(json));

        return arabaIlan;
    }

    private MongoCollection<Document> getIlan() {
        return db.getCollection("ilan");
    }


    public void ilanGuncelle(ArabaIlan arabaIlan) {
        String json = toJson(arabaIlan);


        MongoCollection<Document> modeller = getIlan();
        Bson query = new Document("ilanNo", arabaIlan.ilanNo);
        modeller.updateOne(query, new Document("$set", Document.parse(json)));
    }


    public Map<Integer, ArabaIlan> modelinKayitlariniGetir(ObjectId modelId, int yilParam) {
        MongoCollection<Document> modeller = getIlan();

        MongoCursor<Document> modelItr = modeller.find(new Document("modelId", modelId.toString()).append("yil", yilParam)).iterator();

        Map<Integer, ArabaIlan> integerArabaIlanMap = new HashMap<>();

        while (modelItr.hasNext()) {
            Document doc = modelItr.next();


            ObjectId id = doc.getObjectId("_id");
            String modelIdStr = doc.getString("modelId");
            int km = doc.getInteger("km");
            int fiyat = doc.getInteger("fiyat");
            String ilanUrl = doc.getString("ilanUrl");
            String tarihStr = doc.getString("ilanTarhi");
            String baslik = doc.getString("baslik");
            String paket = doc.getString("paket");
            String vites = doc.getString("vites");
            String yakit = doc.getString("yakit");
            int ilanNoInt = doc.getInteger("ilanNo");
            Integer ilandurum = doc.getInteger("ilandurum");
            IlanDurum ilanDurum = IlanDurum.getEnum(ilandurum);


            ArabaIlan arabaIlan = new ArabaIlan(yilParam, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt, paket);
            arabaIlan.vites = vites;
            arabaIlan.yakit = yakit;

            arabaIlan.dbId = id;
            arabaIlan.modelId = modelIdStr;
            arabaIlan.setDurum(ilanDurum);
            integerArabaIlanMap.put(arabaIlan.ilanNo, arabaIlan);
        }
        modelItr.close();
        return integerArabaIlanMap;
    }

}
