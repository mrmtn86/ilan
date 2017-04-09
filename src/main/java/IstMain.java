import db.Repo;
import entity.ArabaModel;
import model.AramaParametre;
import model.ModelinIlanlari;
import model.keybuilder.ArabaIlanKeyBuilder;
import model.keybuilder.ArabaIlanPaketKeyBuilder;
import model.keybuilder.ArabaIlanVitesKeyBuilder;
import model.keybuilder.ArabaIlanYakitKeyBuilder;
import parser.html.AramaParametreBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by mac on 09/04/17.
 */
public class IstMain {
    public static void main(String[] args) {

        Repo repo = new Repo();


        List<ArabaModel> arabaModels = repo.modelleriGetir();

        satirYaz("YIL", "KEY", "AD", " ORT FİYAT", "ORt KM", "TOP ILAN");


        for (ArabaModel arabaModel : arabaModels) {


            for (int yil = AramaParametreBuilder.BASLANGIC_YIL; yil <= AramaParametreBuilder.BITIS_YIL; ++yil) {


                System.out.println(arabaModel.ad + " - " + yil);

                ArabaIlanKeyBuilder keyBuilder = new ArabaIlanPaketKeyBuilder(arabaModel.paketler);
                istatistikYaz(repo, arabaModel, yil, keyBuilder);

                keyBuilder = new ArabaIlanYakitKeyBuilder();
                istatistikYaz(repo, arabaModel, yil, keyBuilder);

                keyBuilder = new ArabaIlanVitesKeyBuilder();
                istatistikYaz(repo, arabaModel, yil, keyBuilder);
            }

        }

    }

    private static AramaParametre istatistikYaz(Repo repo, ArabaModel arabaModel, int yil, ArabaIlanKeyBuilder keyBuilder) {
        AramaParametre aramaParametre = new AramaParametre();
        aramaParametre.yil = yil;
        aramaParametre.arabaModel = arabaModel;
        Map<String, ModelinIlanlari> ilanMap = repo.ilanlariGetir(aramaParametre, keyBuilder);

        for (String key : ilanMap.keySet()) {
            ModelinIlanlari modelinIlanlari = ilanMap.get(key);
            String ad = arabaModel.ad;
            String ortalamaFiyatHespla = String.valueOf(modelinIlanlari.ortalamaFiyatHespla());
            String ortalamaKmHespla = String.valueOf(modelinIlanlari.ortalamaKmHespla());
            String toplamArac = String.valueOf(modelinIlanlari.arabaIlanList.size());
            satirYaz(String.valueOf(yil), key, ad, ortalamaFiyatHespla, ortalamaKmHespla, toplamArac);
        }
        return aramaParametre;
    }

    private static void satirYaz(String yil, String key, String ad, String ortalamaFiyat, String ortalamaKm, String toplamArac) {
        System.out.printf("%-30s  %-10s  %-10s  %-10s \n",
                key, ortalamaFiyat, ortalamaKm, toplamArac);

//        System.out.printf(  "%-10s  %-4s   %-30s  %-10s TL %-10s KM %-10s \n",
//                ad,yil ,key , ortalamaFiyat, ortalamaKm , toplamArac);
    }
}
