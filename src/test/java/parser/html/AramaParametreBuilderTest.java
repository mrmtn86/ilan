package parser.html;

import entity.ArabaModel;
import model.AramaParametre;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by mac on 30/05/17.
 */
public class AramaParametreBuilderTest {
    @Test
    public void parametreleriGetir() throws Exception {


        ArabaModel arabaModel = new ArabaModel("modelAd", "modelUrl", new ObjectId());
        int yil = 2015;
        List<AramaParametre> aramaParametreList = AramaParametreBuilder.parametreleriGetir(arabaModel, yil);

        Assert.assertEquals(8 , aramaParametreList.size());


    }

    @Test
    public void butunParamaeetreleOlusturuldu() throws Exception {

        ArabaModel arabaModel = new ArabaModel("modelAd", "modelurl", null);

        List<AramaParametre> aramaParametreList = AramaParametreBuilder.parametreleriGetir(arabaModel, 2015);



        AramaParametre aramaParametre = new AramaParametre(VitesEnum.Manuel, YakitEnum.Dizel.getValue(), 2010, arabaModel, KimdenEnum.Galeriden);

        String geturlString = aramaParametre.geturlString();

        Assert.assertEquals("modelurl/dizel/Manuel/galeriden?pagingSize=50&a5_min=2010&sorting=date_asc&a5_max=2010&a9620=143038" ,geturlString);
    }

}