package model;

import entity.ArabaModel;
import model.istatistik.ModelIstatistik;
import org.bson.types.ObjectId;
import org.junit.Test;
import util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static parser.html.AramaParametreBuilder.benzinLpg;
import static parser.html.AramaParametreBuilder.duzVites;

/**
 * Created by mac on 29/05/17.
 */
public class IlanPuanlayiciTest {


    public static final String DOLU_PAKET = "dolu paket";
    public static final String BOS_PAKET = "bos paket";

    @Test
    public void getBasePuan() throws Exception {


    }

    @Test
    public void getPuanHepsi() throws Exception {

    }

    @Test
    public void invoke() throws Exception {

        ArabaIlan arabaIlan = new ArabaIlan(2005, 37000, 45000, DateUtil.nowDbDateTime(), "ilan başlık", "url", 33, DOLU_PAKET);
        arabaIlan.ilIlce = "Adana";
        arabaIlan.vites = duzVites();
        arabaIlan.yakit = benzinLpg();
        arabaIlan.yakitPuani = 100;
        arabaIlan.vitesPuani = 100;
        arabaIlan.kmPuani = 100;

        ArabaModel arabaModel = new ArabaModel("model ad", "model url", new ObjectId());
        ArrayList<String> paketler = new ArrayList<>();
        paketler.add(DOLU_PAKET);
        paketler.add(BOS_PAKET);

        Map<String, ModelIstatistik> modelIstatistikMap = new HashMap<>();

        ModelIstatistik modelIstatistik = new ModelIstatistik(arabaModel.id.toString(),arabaIlan.yil,arabaIlan.vites,DateUtil.nowDbDateTime(),120000,45000);
        modelIstatistikMap.put(arabaIlan.vites , modelIstatistik);

          modelIstatistik = new ModelIstatistik(arabaModel.id.toString(),arabaIlan.yil,arabaIlan. yakit,DateUtil.nowDbDateTime(),120000,45000);
        modelIstatistikMap.put(arabaIlan.vites , modelIstatistik);





        arabaModel.paketler = paketler;



        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, arabaIlan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();
        System.out.println(puanHepsi);
    }

    @Test
    public void paunlariGetir() throws Exception {

    }

}