package config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import db.Repo;
import db.TestDbContainer;
import entity.ArabaModel;
import model.ArabaIlan;
import model.istatistik.ModelIstatistik;
import org.bson.Document;
import org.bson.types.ObjectId;
import parser.html.VitesEnum;
import parser.json.JsonParser;
import util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static parser.html.AramaParametreBuilder.benzinLpg;
import static parser.html.AramaParametreBuilder.dizel;

/**
 * Created by mtn on 1.06.2017.
 */
public class AbstructtestDbOperation {


    protected static final String OTOMATIK = "otomatik";
    protected static final String ORTA_PAKET = "orta paket";
    protected static final String DOLU_PAKET = "dolu paket";
    protected static final String BOS_PAKET = "bos paket";
    protected ArabaIlan duzvitesDoluPaket100Ilan;
    protected ArabaModel arabaModel;
    protected Map<String, ModelIstatistik> modelIstatistikMap;
    protected MongoDatabase db = TestDbContainer.getMemoryDb();


    @org.junit.Before
    public void setUp() throws Exception {

        MongoCollection<Document> modelCollection = db.getCollection(Repo.MODEL_COLLECTION_NAME);

        arabaModel = new ArabaModel("arabamodel", "ururll1", new ObjectId());
        arabaModel.kullanimDurumu = 1;

        arabaModel.baslangicYili = 2005;
        arabaModel.bitisYili = 2009;

        List<String> paketler = new ArrayList<>();
        paketler.add(BOS_PAKET);
        paketler.add(DOLU_PAKET);
        arabaModel.paketler = paketler;

        modelCollection.insertOne(Document.parse(JsonParser.toJson(arabaModel)));


        duzvitesDoluPaket100Ilan = new ArabaIlan(2005, 37000, 45000, DateUtil.nowDbDateTime(), "ilan başlık", "url", 33, DOLU_PAKET);
        duzvitesDoluPaket100Ilan.ilIlce = "Tokat";
        duzvitesDoluPaket100Ilan.vites = VitesEnum.Manuel.getValue();
        duzvitesDoluPaket100Ilan.yakit = benzinLpg();
        duzvitesDoluPaket100Ilan.yakitPuani = 100;
        duzvitesDoluPaket100Ilan.vitesPuani = 100;
        duzvitesDoluPaket100Ilan.kmPuani = 100;
        duzvitesDoluPaket100Ilan.paketPuani = 100;


        modelIstatistikMap = new HashMap<>();

        ModelIstatistik modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), duzvitesDoluPaket100Ilan.yil, duzvitesDoluPaket100Ilan.vites, DateUtil.nowDbDateTime(), duzvitesDoluPaket100Ilan.km, duzvitesDoluPaket100Ilan.fiyat);
        modelIstatistikMap.put(modelIstatistik.key, modelIstatistik);

        modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), duzvitesDoluPaket100Ilan.yil, duzvitesDoluPaket100Ilan.yakit, DateUtil.nowDbDateTime(), duzvitesDoluPaket100Ilan.km, duzvitesDoluPaket100Ilan.fiyat);
        modelIstatistikMap.put(modelIstatistik.key, modelIstatistik);


        modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), duzvitesDoluPaket100Ilan.yil, DOLU_PAKET, DateUtil.nowDbDateTime(), duzvitesDoluPaket100Ilan.km, duzvitesDoluPaket100Ilan.fiyat + 3000);
        modelIstatistikMap.put(modelIstatistik.key, modelIstatistik);


        modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), duzvitesDoluPaket100Ilan.yil, BOS_PAKET, DateUtil.nowDbDateTime(), duzvitesDoluPaket100Ilan.km + 9000, duzvitesDoluPaket100Ilan.fiyat - 5000);
        modelIstatistikMap.put(modelIstatistik.key, modelIstatistik);

        modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), duzvitesDoluPaket100Ilan.yil, OTOMATIK, DateUtil.nowDbDateTime(), duzvitesDoluPaket100Ilan.km, duzvitesDoluPaket100Ilan.fiyat);
        modelIstatistikMap.put(modelIstatistik.key, modelIstatistik);

        modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), duzvitesDoluPaket100Ilan.yil, dizel(), DateUtil.nowDbDateTime(), duzvitesDoluPaket100Ilan.km, duzvitesDoluPaket100Ilan.fiyat);
        modelIstatistikMap.put(modelIstatistik.key, modelIstatistik);


    }
}
