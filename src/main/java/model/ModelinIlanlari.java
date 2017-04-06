package model;

import db.Repo;
import entity.ArabaModel;
import error.IlanException;
import parser.html.HtmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mtn on 31.03.2017.
 */
public class ModelinIlanlari {

    public static final int PUAN_LIMIT = 170;
    static int MAX_ARAC_FIYATI = 38000;

    static List<Integer> karaListe = new ArrayList<Integer>() {{
        add(403080735);
        add(407785432);
        add(405735596);
        add(365975880);
        add(411877744);
        add(404634934);
        add(405275624);
        add(409178825);
        add(411703245);
        add(399692863);
        add(412580599);
        add(407296283);
        add(418170659);
        add(417592735);
        add(385306471);
        add(417448859);
        add(407977828);
        add(403794852);
        add(406594195);
        add(398934632);
        add(404522766);
        add(400769020);
        add(404840463);
        add(409044941);
        add(418729819);
    }};
    private final ArabaModel arabaModel;
    private final int yil;
    private final String vites;
    private final String yakit;
    public int ortalamaKm = 0;
    public int ortalamaFiyat = 0;
    //  public List<ArabaIlan> ilanlar = new ArrayList<>();
    public Map<Integer, ArabaIlan> arabaIlanMap;


    public ModelinIlanlari(ArabaModel arabaModel, int yil, String vites, String yakit) {
        this.vites = vites;
        this.yakit = yakit;

        Repo repo = new Repo();

        arabaIlanMap = repo.modelinKayitlariniGetir(arabaModel.id, yil);
        this.arabaModel = arabaModel;
        this.yil = yil;

    }

    private static boolean karaListedemi(ArabaIlan arabaIlan) {

        return karaListe.contains(arabaIlan.ilanNo);
    }

//    public void ilanEkle(ArabaIlan araba) {
//
//        if (araba.yil != yil) {
//            throw new IlanException("yil tutumuyor");
//        }
//
//        if (!arabaModel.id.toString().equals(araba.modelId)) {
//            throw new IlanException("model tutmuyor");
//        }
//
//        ilanlar.add(araba);
//
//        ortalamaKm += (araba.km - ortalamaKm) / ilanlar.size();
//        ortalamaFiyat += (araba.fiyat - ortalamaFiyat) / ilanlar.size();
//    }

//    public int toplamArac() {
//        return ilanlar.size();
//    }

    public int ilanPuaniHesapla(ArabaIlan arabaIlan) {

        arabaIlan.fiyatPuani = arabaIlan.fiyat * 100 / ortalamaFiyat;
        arabaIlan.kmPuani = arabaIlan.km * 100 / ortalamaKm;

        return arabaIlan.ilanPuani = arabaIlan.kmPuani + arabaIlan.fiyatPuani;
    }

    public List<ArabaIlan> durumDegerlendir() {


        List<ArabaIlan> makulIlanlar = new ArrayList<>();
        if (arabaIlanMap == null || arabaIlanMap.size() == 0) {
            return makulIlanlar;
        }

        ortalamalrihesapla();

        Repo repo = new Repo();


        for (ArabaIlan ilanDb : arabaIlanMap.values()) {


            IlanDurum ilanDurumDb = ilanDb.getDurum();

            if (ilanDurumDb == null || 0 == ilanDb.ilanPuani) {
                ilanDurumDb = ilanDurumBelirle(ilanDb);
                ilanDb.setDurum(ilanDurumDb);

                repo.ilanGuncelle(ilanDb);
            }

            //arabada kusur bulamadık ekleyelim
            if (ilanDb.getDurum() == IlanDurum.Uygun)
                makulIlanlar.add(ilanDb);
        }

        makulIlanlar.sort(new IlanPuanComperator());
        return makulIlanlar;
    }

    private void ortalamalrihesapla() {
        for (ArabaIlan arabaIlan : arabaIlanMap.values()) {
            ortalamaFiyat += arabaIlan.fiyat;
            ortalamaKm += arabaIlan.km;
        }
        ortalamaFiyat = ortalamaFiyat / arabaIlanMap.size();
        ortalamaKm = ortalamaKm / arabaIlanMap.size();
    }

    private IlanDurum ilanDurumBelirle(ArabaIlan arabaIlan) {

        int ilanPuani = ilanPuaniHesapla(arabaIlan);

        boolean karaListede = karaListedemi(arabaIlan);

        if (karaListede) {
            return arabaIlan.setDurum(IlanDurum.KaraLisetede);
        }

        if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
            return IlanDurum.MaxFiyatiAsiyor;
        }

        if (ilanPuani > PUAN_LIMIT) {
            return arabaIlan.setDurum(IlanDurum.PuanUygunDegil);

        }
        HtmlParser parser = new HtmlParser();
        return parser.aciklamaTemiz(arabaIlan);

    }
}
