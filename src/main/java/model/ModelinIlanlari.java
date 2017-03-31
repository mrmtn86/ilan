package model;

import org.bson.types.ObjectId;
import parser.html.HtmlParser;

import java.util.ArrayList;
import java.util.List;

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

    public int ortalamaKm = 0;
    public int ortalamaFiyat = 0;
    public int yil = 0;

    public List<ArabaIlan> ilanlar = new ArrayList<>();
    public ObjectId modelId;


    public void ilanEkle(ArabaIlan araba) {
        ilanlar.add(araba);

        ortalamaKm += (araba.km - ortalamaKm) / ilanlar.size();
        ortalamaFiyat += (araba.fiyat - ortalamaFiyat) / ilanlar.size();
    }

    public int toplamArac() {
        return ilanlar.size();
    }

    public int ilanPuaniHesapla(ArabaIlan arabaIlan) {

        int fiyatPuani = arabaIlan.fiyat * 100 / ortalamaFiyat;
        int kmPuani = arabaIlan.km * 100 / ortalamaKm;


        return kmPuani + fiyatPuani;
    }

    public List<ArabaIlan> makulIlanlariGetir() {
        List<ArabaIlan> makulIlanlar = new ArrayList<>();
        for (ArabaIlan arabaIlan : ilanlar) {

            if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
                continue;
            }

            boolean karaListede = karaListedemi(arabaIlan);
            if (karaListede) {
                continue;
            }

            int ilanPuani = ilanPuaniHesapla(arabaIlan);
            arabaIlan.ilanPuani = ilanPuani;

            if (ilanPuani > PUAN_LIMIT) {
                continue;
            }
            HtmlParser parser = new HtmlParser();
            boolean aciklamalarTemiz = parser.aciklamaTemiz(arabaIlan);

            if (!aciklamalarTemiz) {
                continue;
            }

            //arabada kusur bulamadık ekleyelim

            makulIlanlar.add(arabaIlan);

        }

        makulIlanlar.sort(new IlanPuanComperator());
        return makulIlanlar;
    }

    private static boolean karaListedemi(ArabaIlan arabaIlan) {

        return karaListe.contains(arabaIlan.ilanNo);
    }
}
