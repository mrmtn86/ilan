import db.Repo;
import model.ArabaModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    static Logger logger = Logger.getLogger(Main.class.getName());

    static int MAX_ARAC_FIYATI = 38000;
    static List<Integer> karaListe = new ArrayList<Integer>() {{
        add(403080735);
        add(407785432);
        add(405735596);
        add(365975880);
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


    public static void main(String[] args) throws IOException {

        logger.setLevel(Level.INFO);

        Repo repo = new Repo();


        List<ArabaModel> modeller = repo.modelleriGetir();

        List<ArabaIlan> makulIlanlar = new ArrayList<>();


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


                        for (int i = 0; i <= 1000; i = i + 50) {

                            try {
                                String ofsetValue = "";
                                if (i > 0) {
                                    ofsetValue = "&pagingOffset=" + i;
                                }
                                String url = aracUrl + yakit + vites + "?" + sahibinden + ofsetValue + ilanSayisi + yil + trPlaka;

                                Document doc = httpGet(url);
                                Elements arabalar = csstenSec(doc, "searchResultsItem");

                                if (arabalar.size() == 0) {
                                    break;
                                }
                                for (Element element : arabalar) {
                                    try {

                                        ArabaIlan araba = getArabaIlan(element);
                                        if ((araba != null)) {
                                            ilanlar.add(araba);


                                            ortalamaKm += (araba.km - ortalamaKm) / ilanlar.size();
                                            ortalamaFiyat += (araba.fiyat - ortalamaFiyat) / ilanlar.size();
                                        }

                                    } catch (Exception ex) {
                                        ex.printStackTrace();

                                    }
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();

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

                            Document detaySayfasi = httpGet(arabaIlan.ilanUrl);


                            boolean aciklamalarTemiz = aciklamaTaramasiTemiz(detaySayfasi);
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

    private static boolean aciklamaTaramasiTemiz(Document doc) {


        if (aciklamadaVarmi(doc, "ağır hasar kaydı var")) {
            return false;
        }

        if (aciklamadaVarmi(doc, "ÇEKME BELGELİ")) {
            return false;
        }
        if (aciklamadaVarmi(doc, "HASARLI AL")) {
            return false;
        }


        return true;
    }

    private static boolean aciklamadaVarmi(Document doc, String aciklama) {
        Elements select = doc.select("#classifiedDescription");
        Elements select1 = select.select(":contains(" + aciklama.toLowerCase(new Locale("tr")) + ")");
        if (select1.size() > 0) {
            return true;
        }
        select1 = select.select(":contains(" + aciklama.toUpperCase(new Locale("tr")) + ")");

        return select1.size() > 0;

    }

    private static Elements csstenSec(Document doc, String cssQuery) {
        return doc.select("." + cssQuery);
    }

    private static Document httpGet(String url) throws IOException {
        String urlAll = "https://www.sahibinden.com/" + url;
        logger.log(Level.FINER, "url get :  {0}", urlAll);
        return Jsoup.connect(urlAll).get();

    }

    private static int ilanPuaniHesapla(ArabaIlan arabaIlan, int ortalamaFiyat, int ortalamaKm) {

        int fiyatPuani = arabaIlan.fiyat * 100 / ortalamaFiyat;
        int kmPuani = arabaIlan.km * 100 / ortalamaKm;


        return kmPuani + fiyatPuani;
    }

    private static BenzerIlanlar benzerIlanlariGetir(ArabaIlan arabaIlan, List<ArabaIlan> ilanlar) {

        BenzerIlanlar benzerIlanlar = new BenzerIlanlar();
        for (ArabaIlan ilanItr : ilanlar) {
            if (ilanItr.model == arabaIlan.model) {
                benzerIlanlar.IlanEkle(ilanItr);
            }
        }
        return benzerIlanlar;

    }

    private static ArabaIlan getArabaIlan(Element element) {
        String baslik = element.select(".searchResultsTitleValue").text();
        Elements select = element.select(".searchResultsAttributeValue");
        if (select == null || select.size() == 0) {
            return null;
        }

        String ilanNo = element.attributes().iterator().next().getValue();
        int ilanNoInt = Integer.parseInt(ilanNo);
        String yilElement = select.first().text();
        String kmElement = select.get(1).text().replace(".", "");
        String fiyatStr = element.select(".searchResultsPriceValue").text().replace(".", "").replace("TL", "").replace(" ", "");
        String ilanUrl = element.select(".searchResultsSmallThumbnail").first().child(0).attributes().get("href");

        ilanUrl = ilanUrl.substring(1, ilanUrl.length());

        String tarihStr = element.select(".searchResultsDateValue").text();

        int model = Integer.parseInt(yilElement);
        int km = Integer.parseInt(kmElement);

        int fiyatTmp = Integer.parseInt(fiyatStr);
        int fiyat = fiyatTmp;
        return new ArabaIlan(model, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt);
    }
}
