package model;

import db.Repo;
import io.hummer.util.math.MathUtil;
import parser.html.HtmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Comparator.naturalOrder;

/**
 * Created by mtn on 31.03.2017.
 */
public class ModelinIlanlari {

    public static final int PUAN_LIMIT = 90;
    public static final double SIGNIFICANCE_LEVEL = 0.9;
    public static int MAX_ARAC_FIYATI = 38000;

    public static List<Integer> karaListe = new ArrayList<Integer>() {{
        add(403080735);
        add(407785432);
        add(405735596);
        add(365975880);
        add(411877744);
        add(404634934);
        add(405275624);
        add(409178825);
        add(411703245);
        add(409984693);
        add(399692863);
        add(412580599);
        add(407296283);
        add(418170659);
        add(417592735);
        add(385306471);
        add(412279087);
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

    public static List<Integer> istenmiyor = new ArrayList<Integer>() {{
        add(407652445);
        add(397553584);
        add(417476891);
        add(421116948);
        add(420668022);
        add(417333144);
        add(409830835);
        add(419490260);
        add(421339993);
        add(407345449);
        add(419264171);
        add(398370550);
    }};

    public static List<Integer> hasarli = new ArrayList<Integer>() {{
        add(413376359);
        add(335988708);
        add(421276944);
        add(421089910);
        add(418971266);
        add(415384716);
        add(360665039);
        add(410551484);
        add(394145440);
        add(420417059);
        add(420704553);
        add(419097269);
        add(410494596);
        add(421065531);
        add(393351939);
        add(393351939);
        add(417045180);
        add(417235818);
        add(421049707);
        add(415518036);
        add(410961597);
        add(420357500);
        add(408743962);

    }};
    public static List<Integer> yanlisbilgi = new ArrayList<Integer>() {{
        add(421038157);

    }};
    public static String[] kusurluAciklamlar = {"ağır hasar kaydı var",
            "agir hasar kaydı mevcut", "" +
            "ÇEKME BELGELİ",
            "HASARLI AL",
            "Ağır Hasar Kayıtlıdır",
            "AGIR HASAR KAYDI",
            "HASAR KAYDI İŞLENMİŞTİR",
            "agir hasarli gosterilmis",
            "Az hasarlı ",
            "agir hasar kayitlidir",
            "komple boyalıdır",
            "pert kayıtlıdır",
            "AGIR HASAR KAYITLIDIR",
            "ağır hasar kayıtlıdır",
            "HASAR KAYDI MEVCUTTUR",
            "ağır hasar kaydı bulunmakta",
            "AĞIR HASARLI yazıyor",
            " HASAR KAYDI BULUNDUĞUNDAN ",
            "KOMPLE BOYATILMIŞTIR ",
            "şişirilmiş hasar kaydı var",
            "agır hasarlı olarak geçiyor",
            "pert kayıtlı  aldım"};
    public int ortalamaKm = 0;
    public int ortalamaFiyat = 0;
    public List<ArabaIlan> arabaIlanList;
    HtmlParser parser = new HtmlParser();
    private Repo repo;


    public ModelinIlanlari(Repo repo) {

        this.repo = repo;

        arabaIlanList = new ArrayList<>();

    }

    private static boolean karaListedemi(ArabaIlan arabaIlan) {

        return karaListe.contains(arabaIlan.ilanNo);
    }


    public void ilanEkle(ArabaIlan arabaIlan) {

        arabaIlanList.add(arabaIlan);
    }

    public int ilanPuaniHesapla(ArabaIlan arabaIlan) {

        arabaIlan.fiyatPuani = arabaIlan.fiyat * 100 / ortalamaFiyat;
        arabaIlan.kmPuani = arabaIlan.km * 100 / ortalamaKm;

        return arabaIlan.ilanPuani = arabaIlan.kmPuani + arabaIlan.fiyatPuani;
    }

    public List<ArabaIlan> durumDegerlendir() {

        ortalamaFiyatHespla();
        ortalamaKmHespla();

        List<ArabaIlan> makulIlanlar = new ArrayList<>();
        if (arabaIlanList == null || arabaIlanList.size() == 0) {
            return makulIlanlar;
        }

        List<ArabaIlan> guncellenecekIlanlar = new ArrayList<>();

        for (ArabaIlan ilanDb : arabaIlanList) {

            IlanDurum ilanDurumDb = IlanDurum.getEnum(ilanDb.getIlandurum());

            if (ilanDurumDb == null || 0 == ilanDb.ilanPuani) {
                ilanDurumDb = ilanDurumBelirle(ilanDb);
                ilanDb.setDurum(ilanDurumDb);

                //repo.ilanGuncelle(ilanDb);

                guncellenecekIlanlar.add(ilanDb);
            }

            //arabada kusur bulamadık ekleyelim
            if (ilanDb.getDurum() == IlanDurum.Uygun)
                makulIlanlar.add(ilanDb);
        }

        repo.ilanlariGuncelle(guncellenecekIlanlar);

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
        return arabaIlanList.size();
    }

    public int ortalamaFiyatHespla() {

        List<Integer> sayilar = new ArrayList<>();

        for (ArabaIlan arabaIlan : arabaIlanList) {
            if (arabaIlan.getDurum()!= null && arabaIlan.getDurum().ortalamadaKullan) {
                sayilar.add(arabaIlan.fiyat);
            }
        }

        int ort = ortalamaHesapla(sayilar);
        ortalamaFiyat = ort;
        return ort;
    }

    public int ortalamaKmHespla() {
        List<Integer> sayilar = new ArrayList<>();

        for (ArabaIlan arabaIlan : arabaIlanList) {
            if (arabaIlan.getDurum()!= null &&  arabaIlan.getDurum().ortalamadaKullan) {
                sayilar.add(arabaIlan.km);
            }

        }

        int ort = ortalamaHesapla(sayilar);

        ortalamaKm = ort;
        return ort;
    }

    private int ortalamaHesapla(List<Integer> sayilar) {

        sayilar.sort(naturalOrder());

        MathUtil mathUtil = new MathUtil();

        while (mathUtil.getOutlier(sayilar, SIGNIFICANCE_LEVEL) != null) {
            double average = mathUtil.average(sayilar);
            Integer outlier = mathUtil.getOutlier(sayilar, SIGNIFICANCE_LEVEL);
            if (outlier > average) {
                sayilar = sayilar.subList(0, sayilar.size() - 1);
            } else {
                sayilar = sayilar.subList(1, sayilar.size());
            }
        }

        int average = (int) mathUtil.average(sayilar);
        if (average == 0)
            average = 1;
        return average;
    }


}
