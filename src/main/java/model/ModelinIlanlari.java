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

    public static final int PUAN_LIMIT = 150;
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
        add(385306471);
        add(407977828);
        add(403794852);
        add(406594195);
        add(398934632);
        add(404522766);
        add(400769020);
        add(404840463);
        add(409044941);
    }};
    private final ArabaModel arabaModel;
    private final int yil;
    public int ortalamaKm = 0;
    public int ortalamaFiyat = 0;
    public List<ArabaIlan> ilanlar = new ArrayList<>();
    private Map<Integer, ArabaIlan> arabaIlanMap;


    public ModelinIlanlari(ArabaModel arabaModel, int yil) {

        this.arabaModel = arabaModel;
        this.yil = yil;

    }

    private static boolean karaListedemi(ArabaIlan arabaIlan) {

        return karaListe.contains(arabaIlan.ilanNo);
    }

    public void ilanEkle(ArabaIlan araba) {

        if(araba.yil != yil){
            throw new IlanException("yil tutumuyor");
        }

        if (!arabaModel.id.equals(araba.modelId)){
            throw new IlanException("model tutmuyor");
        }

        ilanlar.add(araba);

        ortalamaKm += (araba.km - ortalamaKm) / ilanlar.size();
        ortalamaFiyat += (araba.fiyat - ortalamaFiyat) / ilanlar.size();
    }

    public int toplamArac() {
        return ilanlar.size();
    }

    public int ilanPuaniHesapla(ArabaIlan arabaIlan) {

        arabaIlan.fiyatPuani = arabaIlan.fiyat * 100 / ortalamaFiyat;
        arabaIlan.kmPuani = arabaIlan.km * 100 / ortalamaKm;

        return arabaIlan.ilanPuani = arabaIlan.kmPuani + arabaIlan.fiyatPuani;
    }

    public List<ArabaIlan> durumDegerlendir() {

        Repo repo = new Repo();
        this.arabaIlanMap = repo.modelinKayitlariniGetir(arabaModel.id, yil);

        List<ArabaIlan> makulIlanlar = new ArrayList<>();

        for (ArabaIlan arabaIlan : ilanlar) {

            ArabaIlan ilanDb = arabaIlanMap.get(arabaIlan.ilanNo);

            IlanDurum ilanDurumDb = ilanDb.durum;

            if (ilanDurumDb == IlanDurum.Belirsiz) {
                ilanDurumDb = ilanDurumBelirle(arabaIlan);
                ilanDb.durum = ilanDurumDb;
                ilanDb.fiyatPuani = arabaIlan.fiyatPuani;
                ilanDb.kmPuani = arabaIlan.kmPuani;
                ilanDb.ilanPuani = arabaIlan.ilanPuani;

                repo.ilanGuncelle(ilanDb);
            }

            //arabada kusur bulamadık ekleyelim
            makulIlanlar.add(arabaIlan);
        }

        makulIlanlar.sort(new IlanPuanComperator());
        return makulIlanlar;
    }

    private IlanDurum ilanDurumBelirle(ArabaIlan arabaIlan) {


        int ilanPuani = ilanPuaniHesapla(arabaIlan);

        if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
            return IlanDurum.MaxFiyatiAsiyor;
        }

        boolean karaListede = karaListedemi(arabaIlan);

        if (karaListede) {
            return arabaIlan.durum = IlanDurum.KaraLisetede;
        }


        if (ilanPuani > PUAN_LIMIT) {
            return arabaIlan.durum = IlanDurum.PuanUygunDegil;

        }
        HtmlParser parser = new HtmlParser();
        return parser.aciklamaTemiz(arabaIlan);

    }
}
