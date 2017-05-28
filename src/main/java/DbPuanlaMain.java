import db.Repo;
import entity.ArabaModel;
import file.DosyaIslemsici;
import model.*;
import model.istatistik.ModelIstatistik;
import parser.html.HtmlParser;
import util.DateUtil;
import util.MatUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static model.ModelinIlanlari.kusurluAciklamlar;


/**
 * Created by mtn on 6.04.2017.
 */
public class DbPuanlaMain {

    public static final Locale TR_LOCALE = new Locale("tr");
    public static final int PUAN_LIMIT = 93;
    static final int BITIS_YIL = 2017;
    public static List<Integer> istenmiyorList;
    public static List<Integer> hasarliList;
    public static int MAX_ARAC_FIYATI = 38000;
    public static int KM_PUAN_LIMIT = 135;
    private static Logger logger = Logger.getLogger(DbPuanlaMain.class.getName());

    public static void main(String[] args) throws IOException {

        istenmiyorList = DosyaIslemsici.uygunsuzListeGetir("istenmiyor");
        hasarliList = DosyaIslemsici.uygunsuzListeGetir("hasarli");

        logger.setLevel(Level.WARNING);
        Repo repo = new Repo();

        List<ArabaModel> modeller = repo.modelleriGetir();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("makulIlanlar.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(DateUtil.nowDbDateTime());
        writer.println(DateUtil.nowDbDateTime());

        int i = 1;

        for (ArabaModel arabaModel : modeller) {

            System.out.println(arabaModel.ad);
            writer.println(arabaModel.ad);

            List<Integer> ortpuan = new ArrayList<>();
            List<Integer> ortpuanHepsi = new ArrayList<>();

            List<ArabaIlan> makulIlanlar = new ArrayList<>();


            for (int yilParam = arabaModel.baslangicYili; yilParam <= BITIS_YIL; yilParam++) {

                AramaParametre aramaParametre = new AramaParametre();
                aramaParametre.arabaModel = arabaModel;
                aramaParametre.yil = yilParam;
                aramaParametre.yayinda = true;

                Map<String, ModelIstatistik> stringModelIstatistikMap = repo.istatistikGetir(aramaParametre.arabaModel.id.toString(), yilParam);


                ModelinIlanlari modelinIlanlari = repo.ilanlariGetir(aramaParametre);


                for (ArabaIlan arabaIlan : modelinIlanlari.arabaIlanList) {

                    IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, stringModelIstatistikMap, arabaIlan).invoke();
                    int puanHepsi = ilanPuanlayici.getPuanHepsi();
                    boolean arabaBos = ilanPuanlayici.isArabaBos();

                    int basePuan = ilanPuanlayici.getBasePuan();

                    arabaIlan.ilanPuani = puanHepsi;


                    arabaIlan.setDurum(ilanDurumBelirle(arabaIlan));


                    if (arabaIlan.getDurum().equals(IlanDurum.Uygun) && !arabaBos) {
                        makulIlanlar.add(arabaIlan);
                    }

                    ortpuan.add(basePuan);
                    ortpuanHepsi.add(puanHepsi);
                    writer.println(ilanPuanlayici.paunlariGetir());
//                    logger.log(Level.INFO, "{0} puanHepsi:{1} basePuan:{2} yakitPuani:{3} vitesPuani:{4} " +
//                                    "paketPuani:{5} kmPuani:{6} gunPuan:{7} sehirPuani:{8} arabaBosPaun:{9}",
//                            new Object[]{arabaIlan, puanHepsi, basePuan, yakitPuani, vitesPuani,
//                                    paketPuani, kmPuani, gunPuan, sehirPuani, arabaBosPaun});

                }
                repo.ilanlariGuncelle(modelinIlanlari.arabaIlanList);
            }

            System.out.println("ort : " + MatUtil.ortalamaHesapla(ortpuan) + "  ort hepsi : " + MatUtil.ortalamaHesapla(ortpuanHepsi) + "toplam : " + makulIlanlar.size());

            makulIlanlar.sort(new IlanPuanComperator());

            for (ArabaIlan arabaIlan : makulIlanlar) {
                System.out.println(i + ". " + arabaIlan);
                System.out.println(arabaIlan.aciklama + "\n");
                writer.println(i++ + ". " + arabaIlan);
            }
        }

        writer.close();
    }


    private static IlanDurum ilanDurumBelirle(ArabaIlan arabaIlan) {

        boolean istenmiyorMu = istenmiyorList.contains(arabaIlan.ilanNo);
        if (istenmiyorMu) {
            return IlanDurum.Istenmiyor;
        }
        boolean hasarli = hasarliList.contains(arabaIlan.ilanNo);
        if (hasarli) {
            return IlanDurum.Hasarli;
        }

        if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
            return IlanDurum.MaxFiyatiAsiyor;
        }
        if (arabaIlan.ilanPuani > PUAN_LIMIT) {
            return IlanDurum.PuanUygunDegil;
        }
        if (arabaIlan.kmPuani > KM_PUAN_LIMIT) {
            return IlanDurum.KmPuanUygunDegil;
        }
        if (arabaIlan.kimden.equals("Galeriden")) {
            return IlanDurum.Galeriden;
        }

        if (arabaIlan.aciklama == null || arabaIlan.aciklama.isEmpty()) {
            HtmlParser parser = new HtmlParser();
            arabaIlan.aciklama = parser.aciklamayiGetir(arabaIlan);
        }

        if (arabaIlan.aciklama != null) {
            String aciklamaUp = arabaIlan.aciklama.replace(" ", "").toUpperCase(new Locale("tr"));
            String aciklamaLow = arabaIlan.aciklama.replace(" ", "").toLowerCase(new Locale("tr"));

            for (String kusurluAciklama : kusurluAciklamlar) {

                kusurluAciklama = kusurluAciklama.replace(" ", "").toLowerCase(TR_LOCALE);

                if (aciklamaLow.contains(kusurluAciklama)) {
                    return IlanDurum.AciklamadaUygunsuzlukVar;
                }
                if (aciklamaUp.contains(kusurluAciklama.toUpperCase(TR_LOCALE))) {
                    return IlanDurum.AciklamadaUygunsuzlukVar;
                }
            }
        }
        return IlanDurum.Uygun;
    }
}
