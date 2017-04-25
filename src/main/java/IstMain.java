import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.AramaParametre;
import model.IlanDurum;
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
            writer = new PrintWriter("ist.txt", "UTF-8");
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
                writer.println("\n" + arabaModel.ad + " - " + yil);

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
        aramaParametre.yayinda = true;
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

                int ilandurum = arabaIlan.ilandurum;
                IlanDurum ilanDurum = IlanDurum.getEnum(ilandurum);

                if (ilanDurum != null)
                    // uygunsuz ilanlari ortalamaya almayalim
                {
                    if (ilanDurum.equals(IlanDurum.AciklamadaUygunsuzlukVar) || ilanDurum.equals(IlanDurum.YanlisBilgi) || ilanDurum.equals(IlanDurum.Hasarli) || ilanDurum.equals(IlanDurum.KaraLisetede)) {
                        continue;
                    }
                }

                ortalamayaKatilanAracSayisi++;
                int kmPuan = kmPuanla(ortalamaKm, arabaIlan.km);


                int puan = (arabaIlan.fiyat * 100 / ortalamaFiyat);

                arabaIlan.kmPuani = kmPuan;
                keyBuilder.setKeyPuan(arabaIlan, puan);
                //arabaIlan.yayinda = true;
            }

            String toplamArac = String.valueOf(modelinIlanlari.arabaIlanList.size());
            toplamArac = ortalamayaKatilanAracSayisi + "/" + toplamArac;
            satirYaz(key, String.valueOf(ortalamaFiyat), String.valueOf(ortalamaKm), toplamArac, writer);

            ModelIstatistik modelIstatistik = new ModelIstatistik(arabaModel.id.toString(), yil, key, DateUtil.nowDbDateTime(), ortalamaKm, ortalamaFiyat);
            repo.istatistikKaydet(modelIstatistik);

            repo.ilanlariGuncelle(modelinIlanlari.arabaIlanList);

        }
    }

    private static int kmPuanla(int ortalamaKm, int km) {
        int kmDilim = 1000;

        if (ortalamaKm >= 200000) {
            kmDilim = 35000;
        } else if (160000 <= ortalamaKm && ortalamaKm < 200000) {
            kmDilim = 20000;
        } else if (80000 <= ortalamaKm && ortalamaKm < 160000) {
            kmDilim = 12000;
        } else if (40000 <= ortalamaKm && ortalamaKm < 800000) {
            kmDilim = 8000;
        } else if (20000 <= ortalamaKm && ortalamaKm < 40000) {
            kmDilim = 5000;
        } else if (10000 <= ortalamaKm && ortalamaKm < 20000) {
            kmDilim = 3000;
        } else if (5000 <= ortalamaKm && ortalamaKm < 10000) {
            kmDilim = 2000;
        }

        int fark = ortalamaKm - km;

        double carpan = 5.0;

        // eger ortalamanin uzaerinde surdu ise carpani artiriyoruz
        // boylece sacma kmsi cok ilanlarin iyi puan alma sansi azalcak
        if (fark < 0) {
            carpan = 10;
        }
        return 100 - (int) (((fark * carpan) / (kmDilim)));
    }

    private static void satirYaz(String key, String ortalamaFiyat, String ortalamaKm, String toplamArac, PrintWriter writer) {
        System.out.printf("%-30s  %-10s  %-10s  %-10s \n",
                key, ortalamaFiyat, ortalamaKm, toplamArac);

        writer.printf("%-30s  %-10s  %-10s  %-10s \n",
                key, ortalamaFiyat, ortalamaKm, toplamArac);

    }
}
