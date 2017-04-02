import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.IlanDurum;
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
    private static final int BASLANGIC_YIL = 2007;
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        logger.setLevel(Level.FINE);

        Repo repo = new Repo();


        List<ArabaModel> modeller = repo.modelleriGetir();
        List<Url> urls = UrlBuilder.getUrls();


        for (ArabaModel arabaModel : modeller) {


            for (int yilParam = BASLANGIC_YIL; yilParam <= BITIS_YIL; yilParam++) {

                for (Url urlItr : urls) {

                    ModelinIlanlari modelinIlanlari = getModelinIlanlari(arabaModel, urlItr, yilParam);

                    logger.log(Level.INFO, "ayarlar : [{0} {1} {2} {3} ] , toplam : {4} , ort km : {5} , ort fiyat : {6}", new Object[]{arabaModel.ad, urlItr.vites, urlItr.yakit, yilParam, modelinIlanlari.toplamArac(), modelinIlanlari.ortalamaKm, modelinIlanlari.ortalamaFiyat});


                    if (modelinIlanlari.toplamArac() == 0) {
                        continue;
                    }

                    List<ArabaIlan> makulIlanlar = modelinIlanlari.durumDegerlendir();

                    makulIlanlar.forEach(System.out::println);

                }
            }
        }

    }


    private static ModelinIlanlari getModelinIlanlari(ArabaModel arabaModel, Url url, int yilParam) {

        String yil = "&a5_min=" + yilParam + "&a5_max=" + yilParam;

        String urlResult = arabaModel.url + url.geturlString() + yil;

        ModelinIlanlari modelinIlanlari = new ModelinIlanlari(arabaModel, yilParam);

        HtmlParser parser = new HtmlParser();

        Repo repo = new Repo();
        Map<Integer, ArabaIlan> arabaIlanMap = repo.modelinKayitlariniGetir(arabaModel.id, yilParam);

        //todo unutma 1000 yap
        for (int i = 0; i <= 5; i = i + 50) {

            String ofsetValue = "";
            if (i > 0) {
                ofsetValue = "&pagingOffset=" + i;
            }
            urlResult +=  ofsetValue;

            List<ArabaIlan> arabaIlanList = parser.arabaIlanlariGetir(urlResult);

            if (arabaIlanList.size() == 0) {
                break;
            }

            for (ArabaIlan arabaIlan : arabaIlanList) {

                if ((arabaIlan != null)) {
                    arabaIlan.modelId = arabaModel.id;

                    ArabaIlan ilanDb = arabaIlanMap.get(arabaIlan.ilanNo);

                    if (ilanDb == null) {
                        ilanDb = repo.IlaniKaydet(arabaIlan);
                    }

                    // ilanin sikintili oldugunu biliyorsak hic eklemeyelim listeye
                    // ortalamay etkilmesin
                    if (ilanDb.durum == IlanDurum.AciklamadaUygunsuzlukVar || ilanDb.durum == IlanDurum.KaraLisetede) {
                        continue;
                    }

                    modelinIlanlari.ilanEkle(arabaIlan);
                }
            }
        }
        return modelinIlanlari;
    }

}
