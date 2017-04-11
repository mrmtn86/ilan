import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.AramaParametre;
import model.ModelinIlanlari;
import model.istatistik.ModelIstatistik;
import model.keybuilder.ArabaIlanKeyBuilder;
import model.keybuilder.ArabaIlanPaketKeyBuilder;
import model.keybuilder.ArabaIlanVitesKeyBuilder;
import model.keybuilder.ArabaIlanYakitKeyBuilder;
import parser.html.AramaParametreBuilder;
import util.DateUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 09/04/17.
 */
public class IstMain {
    public static void main(String[] args) {

        Repo repo = new Repo();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("the-file-name.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<ArabaModel> arabaModels = repo.modelleriGetir();

        satirYaz("KEY", " ORT FİYAT", "ORt KM", "TOP ILAN", writer);


        for (ArabaModel arabaModel : arabaModels) {

            for (int yil = arabaModel.baslangicYili; yil <= AramaParametreBuilder.BITIS_YIL; ++yil) {

                System.out.println("");
                System.out.println(arabaModel.ad + " - " + yil);
                writer.println("\n"+arabaModel.ad + " - " + yil);

                ArabaIlanKeyBuilder paketKeyBuilder = new ArabaIlanPaketKeyBuilder(arabaModel.paketler);
                istatistikYaz(repo, arabaModel, yil, paketKeyBuilder, writer);

                ArabaIlanKeyBuilder yakitKeyBuilder = new ArabaIlanYakitKeyBuilder();
                istatistikYaz(repo, arabaModel, yil, yakitKeyBuilder, writer);

                ArabaIlanKeyBuilder vitesKeyBuilder = new ArabaIlanVitesKeyBuilder();
                istatistikYaz(repo, arabaModel, yil, vitesKeyBuilder, writer);
            }

        }

        writer.close();

    }

    private static void istatistikYaz(Repo repo, ArabaModel arabaModel, int yil, ArabaIlanKeyBuilder keyBuilder, PrintWriter writer) {
        AramaParametre aramaParametre = new AramaParametre();
        aramaParametre.yil = yil;
        aramaParametre.arabaModel = arabaModel;
        Map<String, ModelinIlanlari> ilanMap = repo.ilanlariGetir(aramaParametre, keyBuilder);


        for (String key : ilanMap.keySet()) {
            int ortalamayaKatilanAracSayisi = 0;

            ModelinIlanlari modelinIlanlari = ilanMap.get(key);

            int ortalamaFiyat = modelinIlanlari.ortalamaFiyatHespla();
            int ortalamaKm = modelinIlanlari.ortalamaKmHespla();

            // ortalamalra gore arac key puanlarini belirleyeleim
            for (ArabaIlan arabaIlan : modelinIlanlari.arabaIlanList) {

                String ilanTarhi = arabaIlan.ilanTarhi;
                Date date = DateUtil.dbDateToDate(ilanTarhi);
                int gecenGun = DateUtil.kacGunGecmis(date);

                if (gecenGun > 60 && !arabaIlan.yayinda) { // cok eski ilanlari hesaplamaya katmayalim
                    continue;
                }

                ortalamayaKatilanAracSayisi++;
                int kmPuan = kmPuanla(ortalamaKm, arabaIlan.km);

                int fiyatPuan = arabaIlan.fiyat * 100 / ortalamaFiyat;


                // fiyat puani daha kiymetli
                int puan = (fiyatPuan * 7 + kmPuan*3) / 10;

                arabaIlan.kmPuani = kmPuan;
                keyBuilder.setKeyPuan(arabaIlan, puan);
                //arabaIlan.yayinda = true;
            }

            String toplamArac = String.valueOf(modelinIlanlari.arabaIlanList.size());
            toplamArac = ortalamayaKatilanAracSayisi + "/" + toplamArac;
            satirYaz(key, String.valueOf(ortalamaFiyat), String.valueOf(ortalamaKm), toplamArac, writer );

            ModelIstatistik modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), yil, key, DateUtil.nowDbDateTime(), ortalamaKm, ortalamaFiyat);
            repo.istatistikKaydet(modelIstatistik);

            repo.ilanlariGuncelle(modelinIlanlari.arabaIlanList);

        }
    }

    private static int kmPuanla(int ortalamaKm, int km) {
        int kmDilim = 1000;

        if (ortalamaKm >= 200000) {
            kmDilim = 50000;
        } else if (160000 <= ortalamaKm && ortalamaKm < 200000) {
            kmDilim = 40000;
        } else if (100000 <= ortalamaKm && ortalamaKm < 160000) {
            kmDilim = 30000;
        } else if (60000 <= ortalamaKm && ortalamaKm < 100000) {
            kmDilim = 20000;
        } else if (30000 <= ortalamaKm && ortalamaKm < 60000) {
            kmDilim = 10000;
        } else if (10000 <= ortalamaKm && ortalamaKm < 30000) {
            kmDilim = 5000;
        } else if (5000 <= ortalamaKm && ortalamaKm < 10000) {
            kmDilim = 2500;
        }

        int fark = ortalamaKm - km;

        return 100 - (int) (((fark * 5.0) / (kmDilim)));
    }

    private static void satirYaz(String key, String ortalamaFiyat, String ortalamaKm, String toplamArac, PrintWriter writer) {
        System.out.printf("%-30s  %-10s  %-10s  %-10s \n",
                key, ortalamaFiyat, ortalamaKm, toplamArac);

        writer.printf("%-30s  %-10s  %-10s  %-10s \n",
                key, ortalamaFiyat, ortalamaKm, toplamArac);

    }
}
