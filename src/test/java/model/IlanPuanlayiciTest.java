package model;

import config.AbstructtestDbOperation;
import entity.ArabaModel;
import model.istatistik.ModelIstatistik;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import parser.html.VitesEnum;
import util.DateUtil;

import java.util.*;

import static parser.html.AramaParametreBuilder.benzinLpg;
import static parser.html.AramaParametreBuilder.dizel;

/**
 * Created by mac on 29/05/17.
 */
public class IlanPuanlayiciTest extends AbstructtestDbOperation {





    @Test
    public void getBasePuan() throws Exception {


    }

    @Test
    public void getPuanHepsi() throws Exception {

    }

    @Test
    public void invokeGenelKontrol() throws Exception {


        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(0, ilanPuanlayici.getSehirPuani());
        Assert.assertEquals(0, ilanPuanlayici.getGunPuan());
        Assert.assertEquals(0, ilanPuanlayici.getArabaBosPaun());
        Assert.assertEquals(0, ilanPuanlayici.getArabaDoluPaun());

        Assert.assertEquals(100, ilanPuanlayici.getYakitPuani());
        Assert.assertEquals(100, ilanPuanlayici.getVitesPuani());
        Assert.assertEquals(100, ilanPuanlayici.getPaketPuani());
        Assert.assertEquals(100, puanHepsi);
    }

    @Test
    public void invokeKotuSehirKontrol() throws Exception {

        duzvitesDoluPaket100Ilan.ilIlce = "Adana";

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(6, ilanPuanlayici.getSehirPuani());
        Assert.assertEquals(106, puanHepsi);
    }

    @Test
    public void invokeIyiSehirKontrol() throws Exception {

        duzvitesDoluPaket100Ilan.ilIlce = "Eskişehir";

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(-3, ilanPuanlayici.getSehirPuani());
        Assert.assertEquals(97, puanHepsi);
    }

    @Test
    public void invokeGunEskiIlanKontrol() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -6);
        duzvitesDoluPaket100Ilan.ilanTarhi = DateUtil.dateToDbDateTime(cal.getTime());

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(2, ilanPuanlayici.getGunPuan());
        Assert.assertEquals(102, puanHepsi);
    }

    @Test
    public void invokeGun60GundenEskiIlanKontrol() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -100);
        duzvitesDoluPaket100Ilan.ilanTarhi = DateUtil.dateToDbDateTime(cal.getTime());

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(20, ilanPuanlayici.getGunPuan());
        Assert.assertEquals(120, puanHepsi);
    }

    @Test
    public void invokeGunYeniIlanKontrol() throws Exception {

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(0, ilanPuanlayici.getGunPuan());
        Assert.assertEquals(100, puanHepsi);
    }

    @Test
    public void invokeBosPaketKontrol() throws Exception {

        duzvitesDoluPaket100Ilan.paket = BOS_PAKET;

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(5, ilanPuanlayici.getArabaBosPaun());
        Assert.assertEquals(105, puanHepsi);
    }

    @Test
    public void invokeDoluPaketKontrol() throws Exception {
        duzvitesDoluPaket100Ilan.vites = OTOMATIK;
        duzvitesDoluPaket100Ilan.yakit = dizel();

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(-3, ilanPuanlayici.getArabaDoluPaun());
        Assert.assertEquals(97, puanHepsi);
    }

    @Test
    public void invokeAzKmKontrol() throws Exception {

        duzvitesDoluPaket100Ilan.km = duzvitesDoluPaket100Ilan.km - 10000;

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();


        Assert.assertEquals(94, duzvitesDoluPaket100Ilan.kmPuani);
        Assert.assertEquals(98, puanHepsi);
    }


    @Test
    public void invokeCokKmKontrol() throws Exception {
        duzvitesDoluPaket100Ilan.km = duzvitesDoluPaket100Ilan.km + 10000;

        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(112, duzvitesDoluPaket100Ilan.kmPuani);
        Assert.assertEquals(103, puanHepsi);
    }


    // ortalama araba ek ozleikleri  ilan puani

    @Test
    public void invokeKarisikKontrol() throws Exception {
        duzvitesDoluPaket100Ilan.vites = OTOMATIK;
        duzvitesDoluPaket100Ilan.yakit = dizel();
        duzvitesDoluPaket100Ilan.ilIlce = "Eskişehir";
        duzvitesDoluPaket100Ilan.paket = DOLU_PAKET;


        IlanPuanlayici ilanPuanlayici = new IlanPuanlayici(arabaModel, modelIstatistikMap, duzvitesDoluPaket100Ilan).invoke();

        int puanHepsi = ilanPuanlayici.getPuanHepsi();

        Assert.assertEquals(-3, ilanPuanlayici.getSehirPuani());
        Assert.assertEquals(0, ilanPuanlayici.getGunPuan());
        Assert.assertEquals(0, ilanPuanlayici.getArabaBosPaun());
        Assert.assertEquals(-3, ilanPuanlayici.getArabaDoluPaun());

        Assert.assertEquals(100, ilanPuanlayici.getYakitPuani());
        Assert.assertEquals(100, ilanPuanlayici.getVitesPuani());
        Assert.assertEquals(100, ilanPuanlayici.getPaketPuani());
        Assert.assertEquals(94, puanHepsi);
    }


}