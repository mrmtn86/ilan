import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.ModelinIlanlari;
import model.AramaParametre;
import parser.html.AramaParametreBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Created by mtn on 6.04.2017.
 */
public class DbPuanlaMain {

    static final int BITIS_YIL = 2017;
    private static final int BASLANGIC_YIL = 2010;

    public static void main(String[] args) throws IOException {

        Repo repo = new Repo();

        List<ArabaModel> modeller = repo.modelleriGetir();

        for (ArabaModel arabaModel : modeller) {

            for (int yilParam = BASLANGIC_YIL; yilParam <= BITIS_YIL; yilParam++) {

                List<AramaParametre> aramaParametres = AramaParametreBuilder.parametreleriGetir(arabaModel , yilParam);
                     for (AramaParametre aramaParametreItr : aramaParametres) {

                         ModelinIlanlari modelinIlanlari = new ModelinIlanlari(arabaModel, yilParam, aramaParametreItr.vites , aramaParametreItr.yakit);

                         List<ArabaIlan> makulIlanlar = modelinIlanlari.durumDegerlendir();

                         makulIlanlar.forEach(System.out::println);
                     }

                //   logger.log(Level.INFO, "ayarlar : [{0} {1} {2} {3} ] , toplam : {4} , ort km : {5} , ort fiyat : {6}", new Object[]{arabaModel.ad, urlItr.vites, urlItr.yakit, yilParam, modelinIlanlari.toplamArac(), modelinIlanlari.ortalamaKm, modelinIlanlari.ortalamaFiyat});
            }
            // }
        }
    }




    }
