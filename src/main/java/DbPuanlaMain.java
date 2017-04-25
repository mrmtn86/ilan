import db.Repo;
import entity.ArabaModel;
import model.*;
import model.ModelinIlanlari;
import parser.html.HtmlParser;
import util.DateUtil;
import util.MatUtil;

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

        int i = 1;

        for (ArabaModel arabaModel : modeller) {

            System.out.println(arabaModel.ad);
            writer.println(arabaModel.ad);

            List<Integer> ortpuan=new ArrayList<>();
            List<Integer> ortpuanHepsi= new ArrayList<>();

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
                    int sehirPuani = sehirPuaniBelirle(arabaIlan.ilIlce);

                    Date date = DateUtil.dbDateToDate(arabaIlan.ilanTarhi);
                    int gunPuan = DateUtil.kacGunGecmis(date) / 3;

                    // carpanlar tamamen sallamasyon
                    int puanHepsi = ((yakitPuani * 4 + vitesPuani * 3 + paketPuani * 2 + kmPuani * 3) / 12) + gunPuan + sehirPuani;
                    arabaIlan.ilanPuani = puanHepsi;

                    arabaIlan.setDurum(ilanDurumBelirle(arabaIlan));

                    if (arabaIlan.getDurum().equals(IlanDurum.Uygun) && arabaIlan.kimden.equals("Sahibinden")) {
                        if (!benzinlManuelVites(arabaIlan.vites, arabaIlan.yakit)) {
                            //  if(arabaIlan.ilanTarhi.equals("2017.04.21"))
                            makulIlanlar.add(arabaIlan);
                        }
                    }

                    ortpuan.add(arabaIlan.ilanPuani);
                    ortpuanHepsi.add(puanHepsi);
                }


                repo.ilanlariGuncelle(modelinIlanlari.arabaIlanList);

            }



            System.out.println("ort : " + MatUtil.ortalamaHesapla(ortpuan));
            System.out.println("ort hepsi : " + MatUtil.ortalamaHesapla(ortpuanHepsi));


            makulIlanlar.sort(new IlanPuanComperator());




            for (ArabaIlan arabaIlan : makulIlanlar) {
                System.out.println(i + ". " + arabaIlan);
                System.out.println(arabaIlan.aciklama + "\n");
                writer.println(i++ + ". " + arabaIlan);
            }

        }


        writer.close();
    }

    private static int sehirPuaniBelirle(String ilIlce) {

        if (ilIlce.equals("Eskişehir")){
            return -3;
        } else if (ilIlce.equals("İzmir")){
            return -2;
        }else if (ilIlce.equals("Uşak")){
            return -1;
        }  else if (ilIlce.equals("İstanbul")){
            return 1;
        }else if (ilIlce.equals("Gaziantep ")){
            return 5;
        }else if (ilIlce.equals("Mardin")){
            return 6;
        }


        return 0;
    }

    private static IlanDurum ilanDurumBelirle(ArabaIlan arabaIlan) {
        boolean karaListede = ModelinIlanlari.karaListe.contains(arabaIlan.ilanNo);

        if (karaListede) {
            return IlanDurum.KaraLisetede;
        } else if (arabaIlan.fiyat > MAX_ARAC_FIYATI) {
            return IlanDurum.MaxFiyatiAsiyor;
        } else if (arabaIlan.ilanPuani > PUAN_LIMIT) {
            return IlanDurum.PuanUygunDegil;
        }else if (arabaIlan.kmPuani > KM_PUAN_LIMIT) {
            return IlanDurum.KmPuanUygunDegil;
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
