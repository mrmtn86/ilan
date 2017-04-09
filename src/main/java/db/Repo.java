package db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import config.LogLevelContainer;
import entity.ArabaModel;
import model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
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

    private MongoCollection<Document> getIlan() {
        return db.getCollection("ilan");
    }

    public void ilanGuncelle(ArabaIlan arabaIlan) {
        String json = toJson(arabaIlan);
        MongoCollection<Document> ilanlar = getIlan();
        Bson query = new Document("ilanNo", arabaIlan.ilanNo);
        ilanlar.updateOne(query, new Document("$set", Document.parse(json)));
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
        int yil = doc.getInteger("yil");
        ObjectId id = doc.getObjectId("_id");
        String modelIdStr = doc.getString("modelId");
        int km = doc.getInteger("km");
        int fiyat = doc.getInteger("fiyat");
        String ilanUrl = doc.getString("ilanUrl");
        String tarihStr = doc.getString("ilanTarhi");
        String baslik = doc.getString("baslik");
        String paket = doc.getString("paket");
        String vites = doc.getString("vites");
        String kimden = doc.getString("kimden");
        String ilIlce = doc.getString("ilIlce");
        String yakit = doc.getString("yakit");
        String aciklama = doc.getString("aciklama");
        int ilanNoInt = doc.getInteger("ilanNo");
        Integer ilandurum = doc.getInteger("ilandurum");

        IlanDurum ilanDurum = IlanDurum.getEnum(ilandurum);

        ArabaIlan arabaIlan = new ArabaIlan(yil, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt, paket);

        arabaIlan.setDurum(ilanDurum);


        arabaIlan.vites = vites;
        arabaIlan.yakit = yakit;
        arabaIlan.modelId = modelIdStr;
        arabaIlan.ilIlce = ilIlce;
        arabaIlan.kimden = kimden;
        arabaIlan.aciklama = aciklama;

        arabaIlan.dbId = id;
        return arabaIlan;
    }

    private MongoCursor<Document> yildakiIlanlariGetir(ObjectId modelId, int yilParam) {
        MongoCollection<Document> ilanlar = getIlan();

        return ilanlar.find(new Document("modelId", modelId.toString()).append("yil", yilParam)).iterator();

    }

    public Map<String , ModelinIlanlari> modelinKayitlariniGetir(AramaParametre aramaParametreItr) {


        MongoCursor<Document> iterator = getDocumentMongoCursor(aramaParametreItr);
        Map<String, ModelinIlanlari> modelinIlanlariMap = new HashMap<>();

        while (iterator.hasNext()) {
            Document document = iterator.next();

            ArabaIlan arabaIlan = getArabaIlan(document);

            String paket = paketGetir(arabaIlan, aramaParametreItr.arabaModel);
            ModelinIlanlari modelinIlanlari = modelinIlanlariMap.computeIfAbsent(paket, k -> new ModelinIlanlari(aramaParametreItr, this));

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

    private MongoCursor<Document> getDocumentMongoCursor(AramaParametre aramaParametreItr) {
        ArabaModel arabaModel = aramaParametreItr.arabaModel;
        String modelId = arabaModel.id.toString();
        int yil = aramaParametreItr.yil;
        String yakit = aramaParametreItr.yakit;
        String vites = aramaParametreItr.vites;

        MongoIterable<Document> ilanDocs = getIlan()
                .find(new Document("modelId", modelId)
                        .append("yil", yil)
                        .append("yakit", yakit)
                        .append("vites", vites));

        return ilanDocs.iterator();
    }
}
