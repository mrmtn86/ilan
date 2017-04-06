import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.ModelinIlanlari;
import model.Url;
import parser.html.HtmlParser;
import parser.html.UrlBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static final int BITIS_YIL = 2017;
    private static final int BASLANGIC_YIL = 2010;

    private static final String sahibinden = "a706=32474";
    private static final String trPlaka = "&a9620=143038";
    private static final String ilanSayisi = "&pagingSize=50";
    private static final String sort = "&sorting=date_desc";
    private static final String gerigidilecekGun = "&date=3days";

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        logger.setLevel(Level.FINE);

        Repo repo = new Repo();

        List<ArabaModel> modeller = repo.modelleriGetir();


        for (ArabaModel arabaModel : modeller) {

            for (int yilParam = BASLANGIC_YIL; yilParam <= BITIS_YIL; yilParam++) {


                List<Url> urls = UrlBuilder.getUrls(arabaModel.url, yilParam);

                for (Url urlItr : urls) {
                    int guncellenenAracSayisi = dbguncelle(arabaModel, urlItr);

                    logger.log(Level.INFO, "ayarlar : [{0} {1} {2} {3} ] , toplam : {4} ", new Object[]{arabaModel.ad, urlItr.vites, urlItr.yakit, yilParam, guncellenenAracSayisi});
                    //   logger.log(Level.INFO, "ayarlar : [{0} {1} {2} {3} ] , toplam : {4} , ort km : {5} , ort fiyat : {6}", new Object[]{arabaModel.ad, urlItr.vites, urlItr.yakit, yilParam, modelinIlanlari.toplamArac(), modelinIlanlari.ortalamaKm, modelinIlanlari.ortalamaFiyat});

                }
            }
        }
    }

    private static int dbguncelle(ArabaModel arabaModel, Url url) {

        Repo repo = new Repo();

        int yilParam = url.yil;
        String urlResult = url.geturlString();

        Map<Integer, ArabaIlan> arabaIlanMap = repo.modelinKayitlariniGetir(arabaModel.id, yilParam);

        HtmlParser parser = new HtmlParser();

        int ekleAracSayisi = 0;

        //en fazla 1000 ilan gosteriliyor
        for (int i = 0; i <= 1000; i = i + 50) {

            if (i >= 1000)
                logger.warning(arabaModel.ad + " icin 1000 ilan gecildi yil " + yilParam);

            String ofsetValue = "";
            if (i > 0) {
                ofsetValue = "&pagingOffset=" + i;
            }
            urlResult += ofsetValue;

            List<ArabaIlan> arabaIlanList = parser.arabaIlanlariGetir(urlResult);

            if (arabaIlanList.size() == 0) {
                break;
            }

            for (ArabaIlan arabaIlan : arabaIlanList) {

                if ((arabaIlan != null)) {
                    arabaIlan.modelId = arabaModel.id.toString();
                    arabaIlan.vites = url.vites;
                    arabaIlan.yakit = url.yakit;

                    ArabaIlan ilanDb = arabaIlanMap.get(arabaIlan.ilanNo);

                    if (ilanDb == null) {
                        repo.IlaniKaydet(arabaIlan);
                        ekleAracSayisi++;
                    }

                }
            }
        }
        return ekleAracSayisi;
    }

}
