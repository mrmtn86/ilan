import config.LogLevelContainer;
import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.AramaParametre;
import parser.html.AramaParametreBuilder;
import parser.html.HtmlParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static parser.html.AramaParametreBuilder.BITIS_YIL;

//import static parser.html.AramaParametreBuilder.BASLANGIC_YIL;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        logger.setLevel(LogLevelContainer.LogLevel);

        Repo repo = new Repo();
        List<ArabaModel> modeller = repo.modelleriGetir();

        int guncellenenAracSayisi = 0;
        for (ArabaModel arabaModel : modeller) {

            for (int yilParam = arabaModel.baslangicYili; yilParam <= BITIS_YIL; yilParam++) {

                List<AramaParametre> aramaParametres = AramaParametreBuilder.parametreleriGetir(arabaModel, yilParam);

                for (AramaParametre aramaParametreItr : aramaParametres) {
                    aramaParametreItr.yayinda = true;
                    guncellenenAracSayisi += dbguncelle(aramaParametreItr, repo);

                }
            }
        }

        logger.log(Level.INFO, " toplam : {0}", new Object[]{guncellenenAracSayisi});

    }

    private static int dbguncelle(AramaParametre aramaParametre, Repo repo) {

        ArabaModel arabaModel = aramaParametre.arabaModel;

        int yilParam = aramaParametre.yil;

        Map<Integer, ArabaIlan> arabaIlanMap = repo.modelinKayitlariniGetirMap(aramaParametre);

        int ekleAracSayisi = 0;
        int toplamGelenArac = 0;

        List<ArabaIlan> yeniAraclar = new ArrayList<>();

        List<ArabaIlan> arabaIlanList = sayfadanGetir(aramaParametre);

        for (ArabaIlan arabaIlan : arabaIlanList) {

            toplamGelenArac++;
            int ilanNo = arabaIlan.ilanNo;

            ArabaIlan ilanDb = arabaIlanMap.get(ilanNo);

            if (ilanDb != null) {
                logger.log(Level.FINER, "ilan daha once dbye eklenmis : {0}" + ilanDb);
                arabaIlanMap.remove(ilanNo);
                continue;
            }

            // dbde yok ekleyelim
            arabaIlan.modelId = arabaModel.id.toString();
            arabaIlan.vites = aramaParametre.vites;
            arabaIlan.yakit = aramaParametre.yakit;
            arabaIlan.kimden = aramaParametre.satan.toString();
            arabaIlan.yayinda = true;

            yeniAraclar.add(arabaIlan);
            ekleAracSayisi++;
            logger.log(Level.FINER, "yeni ilan dbeklennmek uzere kaydedildi {0}", arabaIlan);
        }

        logger.log(Level.INFO, " [{0} {1} {2} {3} {6} ] , gelen :{5}, eklenen : {4} , url : {7} ",
                new Object[]{arabaModel.ad, aramaParametre.vites, aramaParametre.yakit, yilParam, ekleAracSayisi, toplamGelenArac,
                        aramaParametre.satan, HtmlParser.SAHBINDEN_BASE_URL + aramaParametre.geturlString()});


        // mapte kalan ilanlar yayindan kalkmis demektir
        Collection<ArabaIlan> values = arabaIlanMap.values();
        for (ArabaIlan arabaIlan : values) {
            arabaIlan.yayinda = false;
        }

        if (values.size() > 0) {
            repo.ilanlariGuncelle(values);
        }


        if (yeniAraclar.size() > 0)
            repo.topluKaydet(yeniAraclar);
        return ekleAracSayisi;
    }

    private static List<ArabaIlan> sayfadanGetir(AramaParametre aramaParametre) {

        HtmlParser parser = new HtmlParser();

        String urlResult = aramaParametre.geturlString();

        List<ArabaIlan> arabaIlanListSonuc = new ArrayList<>();

        for (int i = 0; i <= 1000; i = i + 50) {

            if (i >= 1000)
                logger.warning(aramaParametre + " icin 1000 ilan gecildi deger  " + i);

            String ofsetValue = "";
            if (i > 0) {
                ofsetValue = "&pagingOffset=" + i;
            }

            List<ArabaIlan> arabaIlanList = parser.arabaIlanlariGetir(urlResult + ofsetValue);

            arabaIlanListSonuc.addAll(arabaIlanList);

            if (arabaIlanList.size() == 0 || arabaIlanList.size() < 50) {
                break;
            }
        }
        return arabaIlanListSonuc;
    }
}
