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

}