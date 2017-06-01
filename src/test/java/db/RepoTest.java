package db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.AbstructtestDbOperation;
import entity.ArabaModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import parser.json.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 30/05/17.
 */
public class RepoTest extends AbstructtestDbOperation {

    @Test
    public void modelleriGetir() throws Exception {


        Repo repo = new Repo(db);

        List<ArabaModel> arabaModels = repo.modelleriGetir();

        Assert.assertEquals(1, arabaModels.size());
        ArabaModel result = arabaModels.get(0);
        Assert.assertEquals(arabaModel.url, result.url);
        Assert.assertEquals(arabaModel.ad, result.ad);
        Assert.assertEquals(arabaModel.baslangicYili, result.baslangicYili);
        Assert.assertEquals(arabaModel.bitisYili, result.bitisYili);
        Assert.assertArrayEquals(arabaModel.paketler.toArray(), result.paketler.toArray());
    }

    @Test
    public void modelleriGetirKulanimDurumu() throws Exception {
        MongoDatabase db = TestDbContainer.getMemoryDb();


        MongoCollection<Document> modelCollection = db.getCollection(Repo.MODEL_COLLECTION_NAME);

        ArabaModel arabaModel = new ArabaModel("arabamodelPasif", "ururll1Pasif", new ObjectId());
        arabaModel.kullanimDurumu = 0;

        arabaModel.baslangicYili = 2007;
        arabaModel.bitisYili = 2011;

        modelCollection.insertOne(Document.parse(JsonParser.toJson(arabaModel)));

        arabaModel = new ArabaModel("arabamodel", "ururll1", new ObjectId());
        arabaModel.kullanimDurumu = 1;

        arabaModel.baslangicYili = 2005;
        arabaModel.bitisYili = 2009;
        List<String> paketler = new ArrayList<>();
        paketler.add("paket1");
        paketler.add("paket2");
        paketler.add("paket3");
        arabaModel.paketler = paketler;

        modelCollection.insertOne(Document.parse(JsonParser.toJson(arabaModel)));

        Repo repo = new Repo(db);

        List<ArabaModel> arabaModels = repo.modelleriGetir();

        Assert.assertEquals(1, arabaModels.size());
        ArabaModel result = arabaModels.get(0);
        Assert.assertEquals(arabaModel.url, result.url);
        Assert.assertEquals(arabaModel.ad, result.ad);
        Assert.assertEquals(arabaModel.baslangicYili, result.baslangicYili);
        Assert.assertEquals(arabaModel.bitisYili, result.bitisYili);
        Assert.assertArrayEquals(arabaModel.paketler.toArray(), result.paketler.toArray());
    }

}