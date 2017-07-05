package service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.AbstructtestDbOperation;
import db.Repo;
import db.TestDbContainer;
import entity.ArabaModel;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import parser.html.HtmlParser;
import parser.json.JsonParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by mtn on 1.06.2017.
 */
public class MainServiceTest extends AbstructtestDbOperation {
    @Test
    public void ilanlariSayfadanGuncelle() throws Exception {

        HtmlParser htmlParser = Mockito.mock(HtmlParser.class);
        File input = new File("src/test/data/html/civic2007Result.html");
        Document ilanDolu = Jsoup.parse(input, "UTF-8", "http://example.com/");
        input = new File("src/test/data/html/ilanyok.html");
        Document ilanBos = Jsoup.parse(input, "UTF-8", "http://example.com/");
        String ilkUrl = "ururll1/dizel/Otomatik/sahibinden?pagingSize=50&a5_min=2005&sorting=date_asc&a5_max=2005&a9620=143038";

        when(htmlParser.httpGet(any())).thenReturn(ilanBos);
        when(htmlParser.httpGet(ilkUrl)).thenReturn(ilanDolu);

        when(htmlParser.sayfadanGetir(any())).thenCallRealMethod();
        when(htmlParser.arabaIlanlariGetir(any())).thenCallRealMethod();
        when(htmlParser.getArabaIlan(any())).thenCallRealMethod();
        MainService mainService = new MainService(db);

        int toplamSonuc = mainService.ilanlariSayfadanGuncelle(htmlParser);


        //sayfada 50 ilan var bunlarin hepsini dbye eklemesini bekliyoruz
        Assert.assertEquals(50, toplamSonuc);
    }

    @Test
    public void baslangicYiliBitisYiliHepsineBak() throws Exception {
        ArabaModel arabaModel = new ArabaModel("Auris", "auris-url", new ObjectId());

        arabaModel.kullanimDurumu = 1;

        arabaModel.baslangicYili = 2007;
        arabaModel.bitisYili = 2010;

        List<String> paketler = new ArrayList<>();
        paketler.add(BOS_PAKET);
        paketler.add(DOLU_PAKET);
        arabaModel.paketler = paketler;

        MongoCollection<org.bson.Document> modelCollection = db.getCollection(Repo.MODEL_COLLECTION_NAME);
        modelCollection.insertOne(org.bson.Document.parse(JsonParser.toJson(arabaModel)));


        HtmlParser htmlParser = Mockito.mock(HtmlParser.class);
        File input = new File("src/test/data/html/2007-2010-ilanlar.htm");
        Document ilanlar = Jsoup.parse(input, "UTF-8", "http://example.com/");
        when(htmlParser.httpGet(any())).thenReturn(ilanlar);

        when(htmlParser.sayfadanGetir(any())).thenCallRealMethod();
        when(htmlParser.arabaIlanlariGetir(any())).thenCallRealMethod();
        when(htmlParser.getArabaIlan(any())).thenCallRealMethod();

        MainService mainService = new MainService(db);

        int toplamSonuc = mainService.ilanlariSayfadanGuncelle(htmlParser);

    }


}