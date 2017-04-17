import db.Repo;
import entity.ArabaModel;
import model.*;
import model.ModelinIlanlari;
import parser.html.HtmlParser;
import util.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static model.ModelinIlanlari.*;

/**
 * Created by mtn on 6.04.2017.
 */
public class DbPuanlaMain {

    static final int BITIS_YIL = 2017;
   // private static final int BASLANGIC_YIL = 2010;

    private static Logger logger = Logger.getLogger(DbPuanlaMain.class.getName());

    public static void main(String[] args) throws IOException {

        Repo repo = new Repo();

        arabalariPuanla(repo);
    }

    private static void arabalariPuanla(Repo repo) {
        List<ArabaModel> modeller = repo.modelleriGetir();

        List<ArabaIlan> makulIlanlar = new ArrayList<>();
        for (ArabaModel arabaModel : modeller) {

            for (int yilParam = arabaModel.baslangicYili; yilParam <= BITIS_YIL; yilParam++) {

                AramaParametre aramaParametre = new AramaParametre();
                aramaParametre.arabaModel = arabaModel;
                aramaParametre.yil = yilParam;

                ModelinIlanlari modelinIlanlari = repo.ilanlariGetir(aramaParametre);


                for (ArabaIlan arabaIlan : modelinIlanlari.arabaIlanList) {

                    int yakitPuani = arabaIlan.yakitPuani;
                    int vitesPuani = arabaIlan.vitesPuani;
                    int paketPuani = arabaIlan.paketPuani;
                    int kmPuani = arabaIlan.kmPuani;



                    // carpanlar tamamen sallamasyon
                    arabaIlan.ilanPuani = (yakitPuani * 5 + vitesPuani * 3 + paketPuani * 2 ) / 10;


                    arabaIlan.setDurum(ilanDurumBelirle(arabaIlan));



                    if (arabaIlan.getDurum().equals(IlanDurum.Uygun) && arabaIlan.kimden.equals("Sahibinden")) {


//if(arabaIlan.ilanTarhi.equals("2017.04.11"))


                        makulIlanlar.add(arabaIlan);
                    }
                }


                repo.ilanlariGuncelle(modelinIlanlari.arabaIlanList);


            }
        }
        makulIlanlar.sort(new IlanPuanComperator());
        makulIlanlar.forEach(System.out::println);
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
            for (String kusurluAciklama : kusurluAciklamlar) {

                if (arabaIlan.aciklama.contains(kusurluAciklama.toLowerCase(new Locale("tr")))) {
                    return IlanDurum.AciklamadaUygunsuzlukVar;
                }
                if (arabaIlan.aciklama.contains(kusurluAciklama.toUpperCase(new Locale("tr")))) {
                    return IlanDurum.AciklamadaUygunsuzlukVar;
                }
            }
        }

        return IlanDurum.Uygun;
    }


}
