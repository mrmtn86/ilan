import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
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

        satirYaz("KEY", " ORT FİYAT", "ORt KM", "TOP ILAN");


        for (ArabaModel arabaModel : arabaModels) {

            for (int yil = AramaParametreBuilder.BASLANGIC_YIL; yil <= AramaParametreBuilder.BITIS_YIL; ++yil) {

                System.out.println("");
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

    private static void istatistikYaz(Repo repo, ArabaModel arabaModel, int yil, ArabaIlanKeyBuilder keyBuilder) {
        AramaParametre aramaParametre = new AramaParametre();
        aramaParametre.yil = yil;
        aramaParametre.arabaModel = arabaModel;
        Map<String, ModelinIlanlari> ilanMap = repo.ilanlariGetir(aramaParametre, keyBuilder);

        for (String key : ilanMap.keySet()) {
            ModelinIlanlari modelinIlanlari = ilanMap.get(key);
            int ortalamaFiyat = modelinIlanlari.ortalamaFiyatHespla();
            int ortalamaKm = modelinIlanlari.ortalamaKmHespla();
            String toplamArac = String.valueOf(modelinIlanlari.arabaIlanList.size());
            satirYaz(key, String.valueOf(ortalamaFiyat), String.valueOf(ortalamaKm), toplamArac);

            for (ArabaIlan arabaIlan : modelinIlanlari.arabaIlanList) {

                int fiyatPuan = arabaIlan.fiyat * 100 / ortalamaFiyat;
                int kmPuan = arabaIlan.km * 100 / ortalamaKm;

                // fiyat puani daha kiymetli
                int puan = (fiyatPuan * 4 + kmPuan * 6) / 10;

                keyBuilder.setPuan(arabaIlan, puan);

                //repo.ilanGuncelle(arabaIlan);
            }

            repo.ilanlariGuncelle(modelinIlanlari.arabaIlanList);


        }
    }

    private static void satirYaz(String key, String ortalamaFiyat, String ortalamaKm, String toplamArac) {
        System.out.printf("%-30s  %-10s  %-10s  %-10s \n",
                key, ortalamaFiyat, ortalamaKm, toplamArac);
    }
}
