package db;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.result.DeleteResult;
import config.LogLevelContainer;
import entity.ArabaModel;
import model.*;
import model.istatistik.ModelIstatistik;
import model.keybuilder.ArabaIlanKeyBuilder;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import parser.html.HtmlParser;
import parser.html.VitesEnum;
import util.DateUtil;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static parser.json.JsonParser.toJson;

/**
 * Created by mac on 21/03/17.
 */
public class Repo {

    public static final String MODEL_COLLECTION_NAME = "model";
    public static final String ILAN_COLLECTION_NAME = "ilan";
    private static Logger logger = Logger.getLogger(Repo.class.getName());
    private final MongoDatabase db;


    public Repo(MongoDatabase db) {

        Logger mongoLogger = Logger.getLogger("org.mongodb");
        mongoLogger.setLevel(Level.WARNING);

        logger.setLevel(LogLevelContainer.LogLevel);


        this.db = db;

    }

    public void ilanlariBosalt() {

        logger.log(Level.WARNING, " ilanlar siliniyor");
        MongoCollection<Document> ilanlar = getIlan();
        ilanlar.drop();

    }

    public List<ArabaModel> modelleriGetir() {
        MongoCollection<Document> modeller = db.getCollection(MODEL_COLLECTION_NAME);

        Bson query = new Document("kullanimDurumu", KullanimDurumu.Aktif.getIndex());
        MongoCursor<Document> modelItr = modeller.find(query).iterator();

        List<ArabaModel> arabaModels = new ArrayList<>();

        while (modelItr.hasNext()) {
            Document doc = modelItr.next();
            String ad = doc.getString("ad");
            String url = doc.getString("url");
            int baslangicYili = doc.getInteger("baslangicYili");
            int bitisYili = doc.containsKey("bitisYili") ? doc.getInteger("bitisYili") : 2017;
            ObjectId id = doc.getObjectId("_id");
            Object paketObj = doc.get("paketler");

            ArabaModel model = new ArabaModel(ad, url, id);
            model.baslangicYili = baslangicYili;
            model.bitisYili = bitisYili;

            if (paketObj != null) {
                model.paketler = (List<String>) paketObj;
            }

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

        try {
            modeller.insertMany(documentList);
        } catch (MongoBulkWriteException e) {

            boolean silmebasarili = hatalıKaydiSil(e);

            if (silmebasarili) {
                topluKaydet(arabaIlanlar);
            } else
                logger.log(Level.WARNING, "toplu kaydetmede hata {0} hata mesaji : {1}",
                        new Object[]{arabaIlanlar.get(0).modelId, e.getMessage()});

        }
    }

    private boolean hatalıKaydiSil(MongoBulkWriteException e) {

        try {

            String hataliKayitIlanNo = e.getWriteErrors().get(0).getMessage().split(":")[4].split(" ")[1];

            return ilanSil(Long.parseLong(hataliKayitIlanNo));

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public MongoCollection<Document> getIlan() {
        return db.getCollection(ILAN_COLLECTION_NAME);
    }

    public void ilanGuncelle(ArabaIlan arabaIlan) {
        MongoCollection<Document> ilanlar = getIlan();

        Bson query = new Document("ilanNo", arabaIlan.ilanNo);
        String json = toJson(arabaIlan);

        ilanlar.updateOne(query, new Document("$set", Document.parse(json)));
    }

    public boolean ilanSil(long ilanNo) {
        MongoCollection<Document> ilanlar = getIlan();

        Bson query = new Document("ilanNo", ilanNo);


        DeleteResult deleteResult = ilanlar.deleteOne(query);
        return deleteResult.getDeletedCount() == 1;
    }


    public void ilanlariGuncelle(Collection<ArabaIlan> ilanlar) {

        MongoCollection<Document> collection = getIlan();

        List<UpdateOneModel<Document>> writes = new ArrayList<>();


        for (ArabaIlan arabaIlan : ilanlar) {

            arabaIlan.baslik = arabaIlan.baslik.replace("Favorilerime Ekle Favorilerimde Karşılaştır ", "");
            arabaIlan.ilIlce = arabaIlan.ilIlce.split(" ")[0];

            Bson query = new Document("ilanNo", arabaIlan.ilanNo);
            String json = toJson(arabaIlan);

            writes.add(
                    new UpdateOneModel<>(query, new Document("$set", Document.parse(json))));
        }

        BulkWriteResult bulkWriteResult = collection.bulkWrite(writes);
        int guncelenenToplam = bulkWriteResult.getModifiedCount();
        logger.log(Level.FINE, "{0}/{1} guncelenen/toplam", new Object[]{guncelenenToplam, ilanlar.size()});
    }

    public int dbguncelle(AramaParametre aramaParametre, List<ArabaIlan> arabaIlanList) {

        ArabaModel arabaModel = aramaParametre.arabaModel;

        int yilParam = aramaParametre.yil;

        Map<Integer, ArabaIlan> arabaIlanMap = modelinKayitlariniGetirMap(aramaParametre);

        int ekleAracSayisi = 0;
        int toplamGelenArac = 0;

        List<ArabaIlan> yeniAraclar = new ArrayList<>();

        for (ArabaIlan arabaIlan : arabaIlanList) {

            toplamGelenArac++;
            int ilanNo = arabaIlan.ilanNo;

            ArabaIlan ilanDb = arabaIlanMap.get(ilanNo);

            if (ilanDb != null) {
                logger.log(Level.FINER, "ilan daha once dbye eklenmis : {0}" + ilanDb);
                arabaIlanMap.remove(ilanNo);
                continue;
            }

            // dbde yok ekleyelim
            arabaIlan.modelId = arabaModel.id.toString();
            arabaIlan.vites = aramaParametre.vites.getValue();
            arabaIlan.yakit = aramaParametre.yakit;
            arabaIlan.kimden = aramaParametre.satan.toString();
            arabaIlan.yayinda = true;
            arabaIlan.eklenmeTarihi = DateUtil.nowDbDateTime();

            yeniAraclar.add(arabaIlan);
            ekleAracSayisi++;
            logger.log(Level.FINER, "yeni ilan dbeklennmek uzere kaydedildi {0}", arabaIlan);
        }

        logger.log(Level.INFO, " [{0} {1} {2} {3} {6} ] , gelen :{5}, eklenen : {4} , url : {7} ",
                new Object[]{arabaModel.ad, aramaParametre.vites, aramaParametre.yakit, yilParam, ekleAracSayisi, toplamGelenArac,
                        aramaParametre.satan, HtmlParser.SAHBINDEN_BASE_URL + aramaParametre.geturlString()});


        // mapte kalan ilanlar yayindan kalkmis demektir
        Collection<ArabaIlan> values = arabaIlanMap.values();
        for (ArabaIlan arabaIlan : values) {
            arabaIlan.yayinda = false;
        }

        if (values.size() > 0) {
            ilanlariGuncelle(values);
        }


        if (yeniAraclar.size() > 0)
            topluKaydet(yeniAraclar);
        return ekleAracSayisi;
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
        int ilanNoInt = -1;
        try {


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
            ilanNoInt = getInteger(doc, "ilanNo");

            int vitesPuani = getInteger(doc, "vitesPuani");
            int yakitPuani = getInteger(doc, "yakitPuani");
            int paketPuani = getInteger(doc, "paketPuani");
            int kmPuani = getInteger(doc, "kmPuani");
            boolean yayinda = doc.getBoolean("yayinda");
            Integer ilandurum = getInteger(doc, "ilandurum");

            String eklenmeTarihi = doc.containsKey("eklenmeTarihi") ? doc.getString("eklenmeTarihi") : tarihStr;


            IlanDurum ilanDurum = IlanDurum.getEnum(ilandurum);

            ArabaIlan arabaIlan = new ArabaIlan(yil, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt, paket);

            arabaIlan.setDurum(ilanDurum);

            arabaIlan.eklenmeTarihi = eklenmeTarihi;
            arabaIlan.vites = vites;
            arabaIlan.yakit = yakit;
            arabaIlan.modelId = modelIdStr;
            arabaIlan.ilIlce = ilIlce;
            arabaIlan.kimden = kimden;
            arabaIlan.aciklama = aciklama;
            arabaIlan.paketPuani = paketPuani;
            arabaIlan.vitesPuani = vitesPuani;
            arabaIlan.yakitPuani = yakitPuani;
            arabaIlan.kmPuani = kmPuani;
            arabaIlan.yayinda = yayinda;

            arabaIlan.dbId = id;
            return arabaIlan;
        } catch (Exception e) {
            if (ilanNoInt > -1) {
                logger.log(Level.WARNING, "{0} nolu ilan icin hata ", String.valueOf(ilanNoInt));
            }
            throw e;
        }
    }

    private Integer getInteger(Document doc, String key) {
        if (doc.containsKey(key))
            return doc.getInteger(key);
        return 0;
    }


    public ModelinIlanlari ilanlariGetir(AramaParametre aramaParametre) {

        MongoCursor<Document> iterator = getDocumentMongoCursor(aramaParametre);

        ModelinIlanlari modelinIlanlari = new ModelinIlanlari();
        while (iterator.hasNext()) {
            Document document = iterator.next();

            ArabaIlan arabaIlan = getArabaIlan(document);

            if (aramaParametre.yayinda != null && arabaIlan.yayinda != aramaParametre.yayinda) {
                continue;

            }

            modelinIlanlari.ilanEkle(arabaIlan);

        }
        return modelinIlanlari;
    }

    public Map<String, ModelinIlanlari> ilanlariGetir(AramaParametre aramaParametreItr, ArabaIlanKeyBuilder keyBuilder) {

        ModelinIlanlari modelinIlanlari1 = ilanlariGetir(aramaParametreItr);
        Map<String, ModelinIlanlari> modelinIlanlariMap = new HashMap<>();

        for (ArabaIlan arabaIlan : modelinIlanlari1.arabaIlanList) {

            String key = keyBuilder.getKey(arabaIlan);
            ModelinIlanlari modelinIlanlari = modelinIlanlariMap.computeIfAbsent(key, k -> new ModelinIlanlari());

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
        VitesEnum vitesEnum = aramaParametre.vites;
        String vites = vitesEnum == null ? null : vitesEnum.getValue();
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
        if (aramaParametre.satan != null) {
            filter = filter.append("kimden", aramaParametre.satan.toString());
        }


        MongoIterable<Document> ilanDocs = getIlan()
                .find(filter);

        return ilanDocs.iterator();
    }


    public void istatistikKaydet(ModelIstatistik modelIstatistik) {

        MongoCollection<Document> modelIsatitistik = db.getCollection("modelIsatitistik");


        Document filter = new Document("modelId", modelIstatistik.modelId)
                .append("yil", modelIstatistik.yil).append("key", modelIstatistik.key);

        MongoIterable<Document> documents = modelIsatitistik.find(filter);

        if (documents.iterator().hasNext()) {
            modelIsatitistik.updateMany(filter, new Document("$set", new Document("aktif", false)));
        }

        String json = toJson(modelIstatistik);

        modelIsatitistik.insertOne(Document.parse(json));
    }

    public Map<String, ModelIstatistik> istatistikGetir(String modelId, int yil) {

        return istatistikGetir(modelId, yil, null);

    }

    public Map<String, ModelIstatistik> istatistikGetir(String modelId, int yil, String key) {

        MongoCollection<Document> modelIsatitistik = db.getCollection("modelIsatitistik");
        Document filter = new Document("modelId", modelId)
                .append("yil", yil).append("aktif", true);


        if (key != null) {
            filter.append("key", key);
        }


        MongoIterable<Document> documents = modelIsatitistik.find(filter);

        Map<String, ModelIstatistik> stringModelIstatistikMap = new HashMap<>();

        MongoCursor<Document> iterator = documents.iterator();
        while (iterator.hasNext()) {
            Document istDoc = iterator.next();

            String istTarihi = istDoc.getString("istTarihi");
            int ortKm = istDoc.getInteger("ortKm");
            int ortFiyat = istDoc.getInteger("ortFiyat");
            String keyIst = istDoc.getString("key");

            ModelIstatistik modelIstatistik = new ModelIstatistik(modelId, yil, keyIst, istTarihi, ortKm, ortFiyat);

            stringModelIstatistikMap.put(keyIst, modelIstatistik);
        }

        return stringModelIstatistikMap;
    }

    public void butunIlanlariyayindaYap() {
        MongoCollection<Document> ilanlar = getIlan();

        ilanlar.updateMany(new Document(), new Document("$set", new Document("yayinda", true)));
    }


}
