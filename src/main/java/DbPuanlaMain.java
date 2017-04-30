import db.Repo;
import entity.ArabaModel;
import model.*;
import model.ModelinIlanlari;
import parser.html.HtmlParser;
import util.DateUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static model.ModelinIlanlari.*;
import static parser.html.AramaParametreBuilder.benzinlManuelVites;

/**
 * Created by mtn on 6.04.2017.
 */
public class DbPuanlaMain {

    public static final Locale TR_LOCALE = new Locale("tr");
    static final int BITIS_YIL = 2017;
    // private static final int BASLANGIC_YIL = 2010;
    private static Logger logger = Logger.getLogger(DbPuanlaMain.class.getName());

    public static void main(String[] args) throws IOException {

        Repo repo = new Repo();

        arabalariPuanla(repo);
    }

    private static void arabalariPuanla(Repo repo) {
        List<ArabaModel> modeller = repo.modelleriGetir();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("makulIlanlar.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(DateUtil.nowDbDateTime());
        writer.println(DateUtil.nowDbDateTime());

        for (ArabaModel arabaModel : modeller) {

            System.out.println(arabaModel.ad);
            writer.println(arabaModel.ad);
            List<ArabaIlan> makulIlanlar = new ArrayList<>();
            for (int yilParam = arabaModel.baslangicYili; yilParam <= BITIS_YIL; yilParam++) {

                AramaParametre aramaParametre = new AramaParametre();
                aramaParametre.arabaModel = arabaModel;
                aramaParametre.yil = yilParam;
                aramaParametre.yayinda = true;

                ModelinIlanlari modelinIlanlari = repo.ilanlariGetir(aramaParametre);


                for (ArabaIlan arabaIlan : modelinIlanlari.arabaIlanList) {

                    int yakitPuani = arabaIlan.yakitPuani;
                    int vitesPuani = arabaIlan.vitesPuani;
                    int paketPuani = arabaIlan.paketPuani;
                    int kmPuani = arabaIlan.kmPuani;

                    Date date = DateUtil.dbDateToDate(arabaIlan.ilanTarhi);
                    int gunPuan = DateUtil.kacGunGecmis(date) / 5;

                    // carpanlar tamamen sallamasyon
                    arabaIlan.ilanPuani = ((yakitPuani * 4 + vitesPuani * 3 + paketPuani * 2 + kmPuani * 3) / 12) + gunPuan;

                    arabaIlan.setDurum(ilanDurumBelirle(arabaIlan));

                    if (arabaIlan.getDurum().equals(IlanDurum.Uygun) && arabaIlan.kimden.equals("Sahibinden")) {
                        if (!benzinlManuelVites(arabaIlan.vites, arabaIlan.yakit)) {
                            //  if(arabaIlan.ilanTarhi.equals("2017.04.21"))
                            makulIlanlar.add(arabaIlan);
                        }
                    }
                }

                repo.ilanlariGuncelle(modelinIlanlari.arabaIlanList);

            }

            makulIlanlar.sort(new IlanPuanComperator());

            int i = 1;


            for (ArabaIlan arabaIlan : makulIlanlar) {
                System.out.println(i + ". " + arabaIlan);
                writer.println(i++ + ". " + arabaIlan);
            }

        }


        writer.close();
    }

    private static IlanDurum ilanDurumBelirle(ArabaIlan arabaIlan) {
        boolean karaListede = ModelinIlanlari.karaListe.contains(arabaIlan.ilanNo);

        if (karaListede) {
            return IlanDurum.KaraLisetede;
        } else if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
            return IlanDurum.MaxFiyatiAsiyor;
        } else if (arabaIlan.ilanPuani > PUAN_LIMIT) {
            return IlanDurum.PuanUygunDegil;
        } else {
            boolean istenmiyorMu = ModelinIlanlari.istenmiyor.contains(arabaIlan.ilanNo);
            if (istenmiyorMu) {
                return IlanDurum.Istenmiyor;
            } else {
                boolean hasarli = ModelinIlanlari.hasarli.contains(arabaIlan.ilanNo);
                if (hasarli) {
                    return IlanDurum.Hasarli;
                } else {
                    boolean yanlisBilgi = ModelinIlanlari.yanlisbilgi.contains(arabaIlan.ilanNo);
                    if (yanlisBilgi) {
                        return IlanDurum.YanlisBilgi;
                    }
                }
            }
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
