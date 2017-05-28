package model;

import entity.ArabaModel;
import model.istatistik.ModelIstatistik;
import util.DateUtil;
import util.MatUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static parser.html.AramaParametreBuilder.benzinliManuelVites;
import static parser.html.AramaParametreBuilder.dizelOtomatikVites;

/**
 * Created by mac on 28/05/17.
 */
public class IlanPuanlayici {
    private ArabaModel arabaModel;
    private Map<String, ModelIstatistik> stringModelIstatistikMap;
    private ArabaIlan arabaIlan;
    private int yakitPuani;
    private int vitesPuani;
    private int paketPuani;
    private int kmPuani;
    private int sehirPuani;
    private int gunPuan;
    private boolean arabaBos;
    private int arabaBosPaun;
    private int basePuan;
    private int puanHepsi;

    public IlanPuanlayici(ArabaModel arabaModel, Map<String, ModelIstatistik> stringModelIstatistikMap, ArabaIlan arabaIlan) {
        this.arabaModel = arabaModel;
        this.stringModelIstatistikMap = stringModelIstatistikMap;
        this.arabaIlan = arabaIlan;
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

    private static int gunPuanHesapla(Date date) {
        int kacGunGecmis = DateUtil.kacGunGecmis(date);
        // 60 tan buyksa 60 diyelim
        if (kacGunGecmis > 60) {
            kacGunGecmis = 60;
        }

        return kacGunGecmis / 3;
    }

    private static boolean isArabaBos(ArabaModel arabaModel, Map<String, ModelIstatistik> stringModelIstatistikMap, ArabaIlan arabaIlan) {

        if (!benzinliManuelVites(arabaIlan.vites, arabaIlan.yakit)) {
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

    private static boolean isarabaOtomatikDizelFull(ArabaModel arabaModel, Map<String, ModelIstatistik> stringModelIstatistikMap, ArabaIlan arabaIlan) {

        if (!dizelOtomatikVites(arabaIlan.vites, arabaIlan.yakit)) {
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

        return ilanPaketIst != null && ilanPaketIst.ortFiyat > paketlerinOrtalamasi;

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
        } else if (80000 <= ortalamaKm && ortalamaKm < 120000) {
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

    public int getYakitPuani() {
        return yakitPuani;
    }

    public int getVitesPuani() {
        return vitesPuani;
    }

    public int getPaketPuani() {
        return paketPuani;
    }

    public int getKmPuani() {
        return kmPuani;
    }

    public int getSehirPuani() {
        return sehirPuani;
    }

    public int getGunPuan() {
        return gunPuan;
    }

    public boolean isArabaBos() {
        return arabaBos;
    }

    public int getArabaBosPaun() {
        return arabaBosPaun;
    }

    public int getBasePuan() {
        return basePuan;
    }

    public int getPuanHepsi() {
        return puanHepsi;
    }

    public IlanPuanlayici invoke() {
        yakitPuani = arabaIlan.yakitPuani;
        vitesPuani = arabaIlan.vitesPuani;
        paketPuani = arabaIlan.paketPuani;
        kmPuani = arabaIlan.kmPuani;
        sehirPuani = sehirPuaniBelirle(arabaIlan.ilIlce);

        Date date = DateUtil.dbDateToDate(arabaIlan.ilanTarhi);
        gunPuan = gunPuanHesapla(date);

        // benzinli manuel dusuk paket ise istemiyoruz
        arabaBos = isArabaBos(arabaModel, stringModelIstatistikMap, arabaIlan);
        arabaBosPaun = arabaBos ? -5 : 0;

        boolean arabaOtomatikDizelFull = isarabaOtomatikDizelFull(arabaModel, stringModelIstatistikMap, arabaIlan);
        int arabaDoluPaun = arabaOtomatikDizelFull ? 3 : 0;

        arabaIlan.kmPuani = kmPuanla(stringModelIstatistikMap, arabaIlan);


        // carpanlar  sallamasyon
        basePuan = (yakitPuani * 4 + vitesPuani * 3 + paketPuani * 2) / 9;
        puanHepsi = ((basePuan * 9 + kmPuani * 3) / 12) + gunPuan + sehirPuani + arabaBosPaun + arabaDoluPaun;
        return this;
    }

    public String paunlariGetir() {
        return arabaIlan + " puanHepsi:" + puanHepsi + " basePuan:" + basePuan + " yakitPuani:" + yakitPuani + " vitesPuani:" + vitesPuani + " " +
                "paketPuani:" + paketPuani + " kmPuani:" + kmPuani + " gunPuan:" + gunPuan + " sehirPuani:" + sehirPuani + " arabaBosPaun:" + arabaBosPaun;
    }
}