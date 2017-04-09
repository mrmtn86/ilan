import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.AramaParametre;
import model.ModelinIlanlari;
import parser.html.AramaParametreBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by mtn on 6.04.2017.
 */
public class DbPuanlaMain {

    static final int BITIS_YIL = 2017;
    private static final int BASLANGIC_YIL = 2010;

    private static Logger logger = Logger.getLogger(DbPuanlaMain.class.getName());

    public static void main(String[] args) throws IOException {

        Repo repo = new Repo();

        List<ArabaModel> modeller = repo.modelleriGetir();

        for (ArabaModel arabaModel : modeller) {

            for (int yilParam = BASLANGIC_YIL; yilParam <= BITIS_YIL; yilParam++) {


                List<AramaParametre> aramaParametres = AramaParametreBuilder.parametreleriGetir(arabaModel, yilParam);
                for (AramaParametre aramaParametreItr : aramaParametres) {

                    Map<String, ModelinIlanlari> modelinIlanlariList = repo.modelinKayitlariniGetir(aramaParametreItr);

                    for (String key : modelinIlanlariList.keySet()) {
                        ModelinIlanlari modelinIlanlari = modelinIlanlariList.get(key);
                        List<ArabaIlan> makulIlanlar = modelinIlanlari.durumDegerlendir();

                        if(makulIlanlar.size()>0)
                        System.out.println("ayarlar : [" + aramaParametreItr + " ," + " toplam :" +
                                modelinIlanlari.toplamArac() + "  ort km :" +
                                modelinIlanlari.ortalamaKm + "  ort fiyat :" +
                                modelinIlanlari.ortalamaFiyat  +" pakert: " +key);

                        makulIlanlar.forEach(System.out::println);
                    }


                }

            }
            // }
        }
    }


}
