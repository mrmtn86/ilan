import db.Repo;
import entity.ArabaModel;
import file.DosyaIslemsici;
import model.*;
import model.ModelinIlanlari;
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

import static model.ModelinIlanlari.*;
import static parser.html.AramaParametreBuilder.benzinlManuelVites;

/**
 * Created by mtn on 6.04.2017.
 */
public class DbPuanlaMain {

    public static final Locale TR_LOCALE = new Locale("tr");
    static final int BITIS_YIL = 2017;
    public static List<Integer> istenmiyorList;
    public static List<Integer> hasarliList;
    public static final int PUAN_LIMIT = 93;
    public static int MAX_ARAC_FIYATI = 38000;
    public static int KM_PUAN_LIMIT = 135;
    private static Logger logger = Logger.getLogger(DbPuanlaMain.class.getName());

    public static void main(String[] args) throws IOException {

        istenmiyorList = DosyaIslemsici.uygunsuzListeGetir("istenmiyor");
        hasarliList = DosyaIslemsici.uygunsuzListeGetir("hasarli");

        logger.setLevel(Level.ALL);
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

                    int yakitPuani = arabaIlan.yakitPuani;
                    int vitesPuani = arabaIlan.vitesPuani;
                    int paketPuani = arabaIlan.paketPuani;
                    int kmPuani = arabaIlan.kmPuani;
                    int sehirPuani = sehirPuaniBelirle(arabaIlan.ilIlce);

                    Date date = DateUtil.dbDateToDate(arabaIlan.ilanTarhi);
                    int gunPuan = gunPuanHesapla(date);

                    // benzinli manuel dusuk paket ise istemiyoruz
                    boolean arabaBos = isArabaBos(arabaModel, stringModelIstatistikMap, arabaIlan);


                    arabaIlan.kmPuani = kmPuanla(stringModelIstatistikMap, arabaIlan);

                    int arabaBosPaun = arabaBos ? 5 : 0;

                    // carpanlar  sallamasyon
                    int basePuan = (yakitPuani * 4 + vitesPuani * 3 + paketPuani * 2) / 9;
                    int puanHepsi = ((basePuan * 9 + kmPuani * 3) / 12) + gunPuan + sehirPuani + arabaBosPaun;
                    arabaIlan.ilanPuani = puanHepsi;


                    arabaIlan.setDurum(ilanDurumBelirle(arabaIlan));


                    if (arabaIlan.getDurum().equals(IlanDurum.Uygun) && !arabaBos) {
                        makulIlanlar.add(arabaIlan);
                    }

                    ortpuan.add(basePuan);
                    ortpuanHepsi.add(puanHepsi);
                    writer.println(arabaIlan+" puanHepsi:"+puanHepsi+" basePuan:"+basePuan+" yakitPuani:"+yakitPuani+" vitesPuani:"+vitesPuani+" " +
                            "paketPuani:"+paketPuani+" kmPuani:"+kmPuani+" gunPuan:"+gunPuan+" sehirPuani:"+sehirPuani+" arabaBosPaun:"+arabaBosPaun);
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

    private static int kmPuanla(Map<String, ModelIstatistik> modelIstatistikMap, ArabaIlan arabaIlan) {

        ModelIstatistik modelIstatistikYakit = modelIstatistikMap.get(arabaIlan.yakit);
        ModelIstatistik modelIstatistikVites = modelIstatistikMap.get(arabaIlan.vites);

        int ortalamaKm = (modelIstatistikVites.ortKm + modelIstatistikYakit.ortKm) / 2;

        int kmDilim = 1000;

        if (ortalamaKm >= 200000) {
            kmDilim = 35000;
        } else if (160000 <= ortalamaKm && ortalamaKm < 200000) {
            kmDilim = 20000;
        } else if (120000 <= ortalamaKm && ortalamaKm < 160000) {
            kmDilim = 12000;
        }else if (80000 <= ortalamaKm && ortalamaKm < 120000) {
            kmDilim = 10000;
        } else if (40000 <= ortalamaKm && ortalamaKm < 800000) {
            kmDilim = 8000;
        } else if (20000 <= ortalamaKm && ortalamaKm < 40000) {
            kmDilim = 5000;
        } else if (10000 <= ortalamaKm && ortalamaKm < 20000) {
            kmDilim = 3000;
        } else if (5000 <= ortalamaKm && ortalamaKm < 10000) {
            kmDilim = 2000;
        }

        int fark = ortalamaKm - arabaIlan.km;

        double carpan = 5.0;

        // eger ortalamanin uzaerinde surdu ise carpani artiriyoruz
        // boylece sacma kmsi cok ilanlarin iyi puan alma sansi azalcak
        if (fark < 0) {
            carpan = 10;
        }
        return 100 - (int) (((fark * carpan) / (kmDilim)));
    }

    private static int gunPuanHesapla(Date date) {
        int kacGunGecmis = DateUtil.kacGunGecmis(date);
        // 60 tan buyksa 60 diyelim
        if (kacGunGecmis > 60) {
            kacGunGecmis = 60;
        }

        return kacGunGecmis / 3;
    }

    private static boolean isArabaBos(ArabaModel arabaModel, Map<String, ModelIstatistik> stringModelIstatistikMap, ArabaIlan arabaIlan) {

        if (!benzinlManuelVites(arabaIlan.vites, arabaIlan.yakit)) {
            return false;
        }


        List<Integer> sayilar = new ArrayList<>();

        ModelIstatistik ilanPaketIst = null;

        for (String paket : arabaModel.paketler) {


            ModelIstatistik modelIstatistik = stringModelIstatistikMap.get(paket);
            if (modelIstatistik == null) {
                continue;
            }

            if (arabaIlan.paket.contains(paket)) {
                ilanPaketIst = modelIstatistik;
            }

            sayilar.add(modelIstatistik.ortFiyat);

        }

        int paketlerinOrtalamasi = MatUtil.ortalamaHesapla(sayilar);

        return ilanPaketIst != null && ilanPaketIst.ortFiyat < paketlerinOrtalamasi;

    }


    private static int sehirPuaniBelirle(String ilIlce) {

        switch (ilIlce) {
            case "Eskişehir":
                return -3;
            case "İzmir":
                return -2;
            case "Uşak":
                return -1;
            case "İstanbul":
                return 3;
            case "Adana ":
                return 6;
            case "Adıyaman ":
                return 6;
            case "Batman ":
                return 7;
            case "Bingöl ":
                return 7;
            case "Bitlis ":
                return 5;
            case "Elazığ":
                return 6;
            case "Diyarbakır":
                return 5;
            case "Hatay ":
                return 6;
            case "Gaziantep ":
                return 5;
            case "Kars ":
                return 4;
            case "Kayseri ":
                return 2;
            case "Kahramanmaraş ":
                return 5;
            case "Mardin":
                return 6;
            case "Mersin":
                return 6;
            case "Nevşehir":
                return 4;
            case "Ordu":
                return 4;
            case "Osmaniye":
                return 6;
            case "Sinop":
                return 3;
            case "Şanlıurfa":
                return 6;
            case "Van":
                return 6;
            default:
                return 0;
        }


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
//        boolean yanlisBilgi = ModelinIlanlari.yanlisbilgi.contains(arabaIlan.ilanNo);
//        if (yanlisBilgi) {
//            return IlanDurum.YanlisBilgi;
//        }
//
//        boolean karaListede = ModelinIlanlari.karaListe.contains(arabaIlan.ilanNo);
//
//
//        if (karaListede) {
//            return IlanDurum.KaraLisetede;
//        }
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
