package model;

import entity.ArabaModel;
import org.junit.Assert;
import org.junit.Test;
import parser.html.AramaParametreBuilder;
import parser.html.KimdenEnum;
import parser.html.VitesEnum;
import parser.html.YakitEnum;

/**
 * Created by mac on 30/05/17.
 */
public class AramaParametreTest {
    @Test
    public void geturlString() throws Exception {

        ArabaModel arabaModel = new ArabaModel("modelAd", "modelurl", null);
        AramaParametre aramaParametre = new AramaParametre(VitesEnum.Manuel, YakitEnum.Dizel.getValue(), 2010, arabaModel, KimdenEnum.Galeriden);

        String geturlString = aramaParametre.geturlString();

        Assert.assertEquals("modelurl/dizel/Manuel/galeriden?pagingSize=50&a5_min=2010&sorting=date_asc&a5_max=2010&a9620=143038" ,geturlString);
    }
}