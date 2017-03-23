import db.Repo;
import model.ArabaIlan;
import model.ArabaModel;
import model.IlanPuanComperator;
import parser.html.HtmlParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static final int PUAN_LIMIT = 150;
    public static final int BASLANGIC_YIL = 2007;
    public static final int BITIS_YIL = 2010;
    static int MAX_ARAC_FIYATI = 38000;
    static List<Integer> karaListe = new ArrayList<Integer>() {{
        add(403080735);
        add(407785432);
        add(405735596);
        add(365975880);
        add(411877744);
        add(404634934);
        add(405275624);
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
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        logger.setLevel(Level.INFO);

        Repo repo = new Repo();


        List<ArabaModel> modeller = repo.modelleriGetir();

        List<ArabaIlan> makulIlanlar = new ArrayList<>();

        HtmlParser parser = new HtmlParser();
        for (ArabaModel arabaModel : modeller) {

            String aracUrl = arabaModel.url;


            String[] yakitSecenek = {"/dizel", "/benzin-lpg,benzin"};
            String[] vitesSecenek = {"/otomatik,yari-otomatik", "/manuel"};


            for (String vites : vitesSecenek) {

                for (String yakit : yakitSecenek) {


                    //benzinli manuel olanlarla ilgilenme
                    if (vites.equals(vitesSecenek[1]) && yakit.equals(yakitSecenek[1])) {
                        continue;
                    }

                    for (int yilParam = BASLANGIC_YIL; yilParam <= BITIS_YIL; yilParam++) {


                        List<ArabaIlan> ilanlar = new ArrayList<>();

                        String sahibinden = "a706=32474";
                        String trPlaka = "&a9620=143038";
                        String yil = "&a5_min=" + yilParam + "&a5_max=" + yilParam;
                        String ilanSayisi = "&pagingSize=50";

                        int ortalamaKm = 0;
                        int ortalamaFiyat = 0;


                        for (int i = 0; i <= 200; i = i + 50) {

                            String ofsetValue = "";
                            if (i > 0) {
                                ofsetValue = "&pagingOffset=" + i;
                            }
                            String url = aracUrl + yakit + vites + "?" + sahibinden + ofsetValue + ilanSayisi + yil + trPlaka;

                            List<ArabaIlan> arabaIlanList = parser.arabaIlanlariGetir(url);

                            if (arabaIlanList.size() == 0) {
                                break;
                            }


                            for (ArabaIlan araba : arabaIlanList) {

                                if ((araba != null)) {
                                    ilanlar.add(araba);


                                    ortalamaKm += (araba.km - ortalamaKm) / ilanlar.size();
                                    ortalamaFiyat += (araba.fiyat - ortalamaFiyat) / ilanlar.size();
                                }
                            }


                        }

                        //   System.out.println(arabaModel.ad + " - " + yakit + " - " + vites + " toplam : " + ilanlar.size());


                        logger.log(Level.INFO, "ayarlar : [{0} {1} {2} {3} ] , toplam : {4} , ort km : {5} , ort fiyat : {6}", new Object[]{arabaModel.ad, vites, yakit, yilParam, ilanlar.size(), ortalamaKm, ortalamaFiyat});


                        if (ilanlar.size() == 0) {
                            continue;
                        }

                        for (ArabaIlan arabaIlan : ilanlar) {

                            if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
                                continue;
                            }

                            boolean karaListede = karaListedemi(arabaIlan);
                            if (karaListede) {
                                continue;
                            }


                            int ilanPuani = ilanPuaniHesapla(arabaIlan, ortalamaFiyat, ortalamaKm);
                            arabaIlan.ilanPuani = ilanPuani;

                            if (ilanPuani > PUAN_LIMIT) {
                                continue;
                            }

                            boolean aciklamalarTemiz = parser.aciklamaTemiz(arabaIlan);

                            if (!aciklamalarTemiz) {
                                continue;
                            }

                            //arabada kusur bulamadık ekleyelim

                            makulIlanlar.add(arabaIlan);

                        }


                    }
                }
            }
        }
        makulIlanlar.sort(new IlanPuanComperator());

        for (ArabaIlan arabaIlan : makulIlanlar) {

            System.out.println(arabaIlan);

        }

    }


    private static boolean karaListedemi(ArabaIlan arabaIlan) {

        return karaListe.contains(arabaIlan.ilanNo);
    }




    private static int ilanPuaniHesapla(ArabaIlan arabaIlan, int ortalamaFiyat, int ortalamaKm) {

        int fiyatPuani = arabaIlan.fiyat * 100 / ortalamaFiyat;
        int kmPuani = arabaIlan.km * 100 / ortalamaKm;


        return kmPuani + fiyatPuani;
    }

}
