package db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.UpdateOneModel;
import config.LogLevelContainer;
import entity.ArabaModel;
import model.*;
import model.keybuilder.ArabaIlanKeyBuilder;
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

    private static Logger logger = Logger.getLogger(Repo.class.getName());
    private final MongoDatabase db;


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

        logger.log(Level.WARNING, " ilanlar siliniyor");
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

    public MongoCollection<Document> getIlan() {
        return db.getCollection("ilan");
    }

    public void ilanGuncelle(ArabaIlan arabaIlan) {
        MongoCollection<Document> ilanlar = getIlan();

        Bson query = new Document("ilanNo", arabaIlan.ilanNo);
        String json = toJson(arabaIlan);

        ilanlar.updateOne(query, new Document("$set", Document.parse(json)));
    }

    //    public void ilanlariGuncelle(List<ArabaIlan> ilanlar) {
//
//        MongoCollection<Document> ilanlar = getIlan();
//
//        List<Document> documentList = new ArrayList<>();
//        for (ArabaIlan arabaIlan : ilanlar) {
//            String json = toJson(arabaIlan);
//
//            Bson query = new Document("ilanNo", arabaIlan.ilanNo);
//            documentList.add(query);
//
//            ilanlar.updateMany(query, new Document("$set", Document.parse(json)));
//        }
//
//
//        ilanlar.updateOne(query, new Document("$set", Document.parse(json)));
//    }
//
    public void ilanlariGuncelle(List<ArabaIlan> ilanlar) {

        MongoCollection<Document> collection = getIlan();

        List<UpdateOneModel<Document>> writes = new ArrayList<>();


        for (ArabaIlan arabaIlan : ilanlar) {
            Bson query = new Document("ilanNo", arabaIlan.ilanNo);
            String json = toJson(arabaIlan);

            writes.add(
                    new UpdateOneModel<>(query, new Document("$set", Document.parse(json))));
        }

        BulkWriteResult bulkWriteResult = collection.bulkWrite(writes);
        int guncelenenToplam = bulkWriteResult.getModifiedCount();
        logger.log(Level.FINE, "{0}/{1} guncelenen/toplam", new Object[]{guncelenenToplam, ilanlar.size()});
    }

    public Map<Integer, ArabaIlan> modelinKayitlariniGetirMap(AramaParametre aramaParametre) {
        MongoCursor<Document> modelItr = getDocumentMongoCursor(aramaParametre);

        Map<Integer, ArabaIlan> integerArabaIlanMap = new HashMap<>();

        while (modelItr.hasNext()) {
            Document doc = modelItr.next();

            ArabaIlan arabaIlan = getArabaIlan(doc);
            integerArabaIlanMap.put(arabaIlan.ilanNo, arabaIlan);
        }
        modelItr.close();
        return integerArabaIlanMap;
    }

    private ArabaIlan getArabaIlan(Document doc) {
        int yil = getInteger(doc, "yil");
        ObjectId id = doc.getObjectId("_id");
        String modelIdStr = doc.getString("modelId");
        int km = getInteger(doc, "km");
        int fiyat = getInteger(doc, "fiyat");
        String ilanUrl = doc.getString("ilanUrl");
        String tarihStr = doc.getString("ilanTarhi");
        String baslik = doc.getString("baslik");
        String paket = doc.getString("paket");
        String vites = doc.getString("vites");
        String kimden = doc.getString("kimden");
        String ilIlce = doc.getString("ilIlce");
        String yakit = doc.getString("yakit");
        String aciklama = doc.getString("aciklama");
        int ilanNoInt = getInteger(doc, "ilanNo");

        int vitesPuani = getInteger(doc, "vitesPuani");
        int yakitPuani = getInteger(doc, "yakitPuani");
        int paketPuani = getInteger(doc, "paketPuani");
        Integer ilandurum = getInteger(doc, "ilandurum");

        IlanDurum ilanDurum = IlanDurum.getEnum(ilandurum);

        ArabaIlan arabaIlan = new ArabaIlan(yil, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt, paket);

        arabaIlan.setDurum(ilanDurum);


        arabaIlan.vites = vites;
        arabaIlan.yakit = yakit;
        arabaIlan.modelId = modelIdStr;
        arabaIlan.ilIlce = ilIlce;
        arabaIlan.kimden = kimden;
        arabaIlan.aciklama = aciklama;
        arabaIlan.paketPuani = paketPuani;
        arabaIlan.vitesPuani = vitesPuani;
        arabaIlan.yakitPuani = yakitPuani;

        arabaIlan.dbId = id;
        return arabaIlan;
    }

    private Integer getInteger(Document doc, String key) {
        if (doc.containsKey(key))
            return doc.getInteger(key);
        return 0;
    }

    private MongoCursor<Document> yildakiIlanlariGetir(ObjectId modelId, int yilParam) {
        MongoCollection<Document> ilanlar = getIlan();

        return ilanlar.find(new Document("modelId", modelId.toString()).append("yil", yilParam)).iterator();

    }

    public Map<String, ModelinIlanlari> ilanlariGetir(AramaParametre aramaParametreItr, ArabaIlanKeyBuilder keyBuilder) {


        MongoCursor<Document> iterator = getDocumentMongoCursor(aramaParametreItr);
        Map<String, ModelinIlanlari> modelinIlanlariMap = new HashMap<>();

        while (iterator.hasNext()) {
            Document document = iterator.next();

            ArabaIlan arabaIlan = getArabaIlan(document);

            String key = keyBuilder.getKey(arabaIlan);
            ModelinIlanlari modelinIlanlari = modelinIlanlariMap.computeIfAbsent(key, k -> new ModelinIlanlari(aramaParametreItr, this));

            modelinIlanlari.ilanEkle(arabaIlan);


        }

        return modelinIlanlariMap;


    }

    public String paketGetir(ArabaIlan arabaIlan, ArabaModel arabaModel) {

        String arabaninEklenecegiPaket = "diger";
        for (String paket : arabaModel.paketler) {
            if (arabaIlan.paket.contains(paket)) {
                arabaninEklenecegiPaket = paket;
                break;
            }
        }
        return arabaninEklenecegiPaket;
    }

    private MongoCursor<Document> getDocumentMongoCursor(AramaParametre aramaParametre) {

        ArabaModel arabaModel = aramaParametre.arabaModel;
        String modelId = arabaModel != null ? arabaModel.id.toString() : null;
        int yil = aramaParametre.yil;
        String yakit = aramaParametre.yakit;
        String vites = aramaParametre.vites;
        int inlanDurum = aramaParametre.ilanDurum;

        Document filter = new Document();
        if (modelId != null) {
            filter = filter.append("modelId", modelId);
        }
        if (yil != 0) {
            filter = filter.append("yil", yil);
        }
        if (yakit != null) {
            filter = filter.append("yakit", yakit);
        }
        if (vites != null) {
            filter = filter.append("vites", vites);
        }
        if (inlanDurum != 0) {
            filter = filter.append("ilandurum", inlanDurum);
        }

        MongoIterable<Document> ilanDocs = getIlan()
                .find(filter);

        return ilanDocs.iterator();
    }
}
