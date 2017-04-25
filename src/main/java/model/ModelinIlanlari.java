package model;

import db.Repo;
import io.hummer.util.math.MathUtil;
import parser.html.HtmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Comparator.naturalOrder;
import static util.MatUtil.ortalamaHesapla;

/**
 * Created by mtn on 31.03.2017.
 */
public class ModelinIlanlari {

    public static final int PUAN_LIMIT = 95;
    public static int MAX_ARAC_FIYATI = 38000;
    public static int KM_PUAN_LIMIT = 130;

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
        add(419758481);
        add(421116948);
        add(420668022);
        add(417333144);
        add(409830835);
        add(421953849);
        add(415574079);
        add(415574079);
        add(415542403);
        add(415574079);
        add(420695919);
        add(419490260);
        add(421339993);
        add(415769133);
        add(407345449);
        add(419264171);
        add(398370550);
    }};

    public static List<Integer> hasarli = new ArrayList<Integer>() {{
        add(413376359);
        add(335988708);
        add(418265375);
        add(419437563);
        add(417625242);
        add(424552149);
        add(425864087);
        add(421276944);
        add(423345726);
        add(424851255);
        add(384601888);
        add(421840448);
        add(422945990);
        add(420014855);
        add(415816777);
        add(416600205);
        add(423205863);
        add(422493566);
        add(422547546);
        add(421753533);
        add(415365558);
        add(421744617);
        add(421104088);
        add(421744617);
        add(422502425);
        add(419514437);
        add(358238522);
        add(418269095);
        add(424855814);
        add(418316823);
        add(418963333);
        add(413748380);
        add(414745016);
        add(415173044);
        add(421953849);
        add(423903604);
        add(382892217);
        add(424467236);
        add(419979763);
        add(390795327);
        add(421089910);
        add(425317668);
        add(423943719);
        add(419849531);
        add(421348405);
        add(417749499);
        add(404825232);
        add(419979763);
        add(425059798);
        add(419047331);
        add(421366268);
        add(423345726);
        add(418050315);
        add(417366756);
        add(418971266);
        add(415384716);
        add(360665039);
        add(410551484);
        add(424114745);
        add(394145440);
        add(420417059);
        add(421727043);
        add(424564750);
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
        add(423511715);

    }};
    public static String[] kusurluAciklamlar = {"ağır hasar kaydı var",
            "agir hasar kaydı mevcut", "" +
            "ÇEKME BELGELİ",
            "HASARLI AL",
            "Ağır Hasar",
            "Ağır Hasar Kayıtlıdır",
            "HASAR KAYDI İŞLENMİŞTİR",
            "SİGORTA ŞİŞİR",
            "HASAR KAYITLI",
            "sigorta şirketinden al",
            "agirhasarli",
            "Hasar kaydi vardir",
            "AĞIR HASAR KAYDI OLDUĞUNDAN",
            "agir hasarli gosterilmis",
            "AGIR HASAR KAYDI ",
            "komple dıştan boyalidir ",
            "kaskodan şişirilmiş ",
            "Az hasarlı ",
            "Az hasarlı ",
            "Hasar Kaydı Bulunmaktadır ",
            "agir hasar kayitlidir",
            "hasar kayıtlı",
            "hasar kaydı vardır",
            "Komple Temizlik Boyası",
            "SİGORTADAN PARA AL",
            "AĞIR HASAR",
            "Komple Boyalı",
            "komple boyali",
            "agir hasar",
            "AGİR HASAR",
            "Ağır hasar kayıtlıdır",
            "komple boyalıdır",
            "hariç boya",
            "pert kayıtlıdır",
            "AGIR HASAR KAYITLIDIR",
            "ağır hasar kayıtlıdır",
            "tümden boyalıdır",
            "HASAR KAYDI MEVCUTTUR",
            "Hasar kaydı vardır",
            "hariç komple boyalı",
            "KOMPLE DIŞTAN BOYALI",
            "ağır hasar kaydı bulunmakta",
            "ARAÇ KOMPLE BOYALI",
            "KOMPLE DIŞTAN BOYALI ",
            "AĞIR HASARLI yazıyor",
            " HASAR KAYDI BULUNDUĞUNDAN ",
            " Hasar kaydı vardır ",
            " hasar  kayıtlıdır ",
            " hariç komple boyalı  ",
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

//    public List<ArabaIlan> durumDegerlendir() {
//
//        ortalamaFiyatHespla();
//        ortalamaKmHespla();
//
//        List<ArabaIlan> makulIlanlar = new ArrayList<>();
//        if (arabaIlanList == null || arabaIlanList.size() == 0) {
//            return makulIlanlar;
//        }
//
//        List<ArabaIlan> guncellenecekIlanlar = new ArrayList<>();
//
//        for (ArabaIlan ilanDb : arabaIlanList) {
//
//            IlanDurum ilanDurumDb = IlanDurum.getEnum(ilanDb.getIlandurum());
//
//            if (ilanDurumDb == null || 0 == ilanDb.ilanPuani) {
//                ilanDurumDb = ilanDurumBelirle(ilanDb);
//                ilanDb.setDurum(ilanDurumDb);
//
//                //repo.ilanGuncelle(ilanDb);
//
//                guncellenecekIlanlar.add(ilanDb);
//            }
//
//            //arabada kusur bulamadık ekleyelim
//            if (ilanDb.getDurum() == IlanDurum.Uygun)
//                makulIlanlar.add(ilanDb);
//        }
//
//        repo.ilanlariGuncelle(guncellenecekIlanlar);
//
//        makulIlanlar.sort(new IlanPuanComperator());
//        return makulIlanlar;
//    }

//    private IlanDurum ilanDurumBelirle(ArabaIlan arabaIlan) {
//
//        int ilanPuani = ilanPuaniHesapla(arabaIlan);
//
//        boolean karaListede = karaListedemi(arabaIlan);
//
//        if (karaListede) {
//            return arabaIlan.setDurum(IlanDurum.KaraLisetede);
//        }
//
//        if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
//            return IlanDurum.MaxFiyatiAsiyor;
//        }
//
//        if (ilanPuani > PUAN_LIMIT) {
//            return arabaIlan.setDurum(IlanDurum.PuanUygunDegil);
//        }
//
//        String aciklamadaYazan = parser.aciklamayiGetir(arabaIlan);
//
//        arabaIlan.aciklama = aciklamadaYazan;
//
//        for (String kusurluAciklama : kusurluAciklamlar) {
//
//            if (aciklamadaYazan.contains(kusurluAciklama.toLowerCase(new Locale("tr")))) {
//                return arabaIlan.setDurum(IlanDurum.AciklamadaUygunsuzlukVar);
//            }
//            if (aciklamadaYazan.contains(kusurluAciklama.toUpperCase(new Locale("tr")))) {
//                return arabaIlan.setDurum(IlanDurum.AciklamadaUygunsuzlukVar);
//            }
//        }
//
//        return arabaIlan.setDurum(IlanDurum.Uygun);

 //   }

    public Object toplamArac() {
        return arabaIlanList.size();
    }

    public int ortalamaFiyatHespla() {

        List<Integer> sayilar = new ArrayList<>();

        for (ArabaIlan arabaIlan : arabaIlanList) {
            if (arabaIlan.getDurum() == null || arabaIlan.getDurum().ortalamadaKullan) {
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
            if (arabaIlan.getDurum() == null || arabaIlan.getDurum().ortalamadaKullan) {
                sayilar.add(arabaIlan.km);
            }

        }

        int ort = ortalamaHesapla(sayilar);

        ortalamaKm = ort;
        return ort;
    }




}
