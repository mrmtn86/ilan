package db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import config.LogLevelContainer;
import entity.ArabaModel;
import model.ArabaIlan;
import model.IlanDurum;
import model.KullanimDurumu;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.IOException;
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

    private static Logger logger = Logger.getLogger(Repo.class.getName());





    public Repo() {

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);

        logger.setLevel(LogLevelContainer.LogLevel);


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

        db = client.getDatabase(uri.getDatabase());

    }

    public void ilanlariBosalt() {

        logger.log(Level.WARNING , " ilanlar siliniyor");
        MongoCollection<Document> ilanlar = getIlan();
        ilanlar.drop();

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

    public void topluKaydet(List<ArabaIlan> arabaIlanlar) {

        MongoCollection<Document> modeller = getIlan();

        List<Document> documentList = new ArrayList<>();
        for (ArabaIlan arabaIlan : arabaIlanlar) {
            String json = toJson(arabaIlan);

            documentList.add(Document.parse(json));
        }


        modeller.insertMany(documentList);

    }

    private MongoCollection<Document> getIlan() {
        return db.getCollection("ilan");
    }

    public void ilanGuncelle(ArabaIlan arabaIlan) {
        String json = toJson(arabaIlan);
        MongoCollection<Document> ilanlar = getIlan();
        Bson query = new Document("ilanNo", arabaIlan.ilanNo);
        ilanlar.updateOne(query, new Document("$set", Document.parse(json)));
    }

    public Map<Integer, ArabaIlan> modelinKayitlariniGetir(ObjectId modelId, int yilParam) {
        MongoCursor<Document> modelItr = yildakiIlanlariGetir(modelId, yilParam);

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

    private MongoCursor<Document> yildakiIlanlariGetir(ObjectId modelId, int yilParam) {
        MongoCollection<Document> ilanlar = getIlan();

        return ilanlar.find(new Document("modelId", modelId.toString()).append("yil", yilParam)).iterator();

    }

}
