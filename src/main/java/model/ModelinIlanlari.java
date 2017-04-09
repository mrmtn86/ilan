package model;

import db.Repo;
import entity.ArabaModel;
import error.IlanException;
import org.jsoup.select.Elements;
import parser.html.HtmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private static String[] kusurluAciklamlar = {"ağır hasar kaydı var",
            "agir hasar kaydı mevcut", "" +
            "ÇEKME BELGELİ",
            "HASARLI AL",
            "Ağır Hasar Kayıtlıdır",
            "AGIR HASAR KAYDI",
            "IR HASAR KAYDI İŞLENMİŞTİR",
            "pert kayıtlıdır",
            "ağır hasar kayıtlıdır",
            "şişirilmiş hasar kaydı var",
            "pert kayıtlı  aldım"};
    private final ArabaModel arabaModel;
    private final int yil;
    private final String vites;
    private final String yakit;
    public int ortalamaKm = 0;
    public int ortalamaFiyat = 0;
    public List<ArabaIlan> arabaIlanMap;
    HtmlParser parser = new HtmlParser();
    private Repo repo;


    public ModelinIlanlari(AramaParametre aramaParametre, Repo repo) {
        this.vites = aramaParametre.vites;
        this.yakit = aramaParametre.yakit;
        this.arabaModel = aramaParametre.arabaModel;
        this.yil = aramaParametre.yil;
        this.repo = repo;

        arabaIlanMap = new ArrayList<>();

    }

    private static boolean karaListedemi(ArabaIlan arabaIlan) {

        return karaListe.contains(arabaIlan.ilanNo);
    }



    public void ilanEkle(ArabaIlan arabaIlan) {

        if (arabaIlan.yil != yil) {
            throw new IlanException("yil tutumuyor");
        }

        if (!arabaModel.id.toString().equals(arabaIlan.modelId)) {
            throw new IlanException("model tutmuyor");
        }


        arabaIlanMap.add(arabaIlan);

        ortalamaKm += (arabaIlan.km - ortalamaKm) / arabaIlanMap.size();
        ortalamaFiyat += (arabaIlan.fiyat - ortalamaFiyat) / arabaIlanMap.size();
    }

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


        for (ArabaIlan ilanDb : arabaIlanMap) {


            IlanDurum ilanDurumDb = IlanDurum.getEnum(ilanDb.getIlandurum());

            if (ilanDurumDb == null || null == ilanDb.ilanPuani) {
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

        String aciklamadaYazan = parser.aciklamayiGetir(arabaIlan);

        arabaIlan.aciklama = aciklamadaYazan;

        for (String kusurluAciklama : kusurluAciklamlar) {

            if (aciklamadaYazan.contains(kusurluAciklama.toLowerCase(new Locale("tr")))) {
                return arabaIlan.setDurum(IlanDurum.AciklamadaUygunsuzlukVar);
            }
            if (aciklamadaYazan.contains(kusurluAciklama.toUpperCase(new Locale("tr")))) {
                return arabaIlan.setDurum(IlanDurum.AciklamadaUygunsuzlukVar);
            }
        }

        return arabaIlan.setDurum(IlanDurum.Uygun);

    }

    public Object toplamArac() {
        return arabaIlanMap.size();
    }
}
