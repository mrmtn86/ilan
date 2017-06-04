package parser.html;

import entity.ArabaModel;
import model.AramaParametre;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtn on 31.03.2017.
 */
public class AramaParametreBuilder {

    public static List<AramaParametre> parametreleriGetir(ArabaModel arabaModel, int yil) {

        List<AramaParametre> aramaParamtreler = new ArrayList<>();
        for (VitesEnum vites : VitesEnum.values()) {

            for (YakitEnum yakit : YakitEnum.values()) {

                for (KimdenEnum satan : KimdenEnum.values()) {
                    AramaParametre aramaParametre = new AramaParametre(vites, yakit.getValue(), yil, arabaModel, satan);

                    aramaParamtreler.add(aramaParametre);
                }
            }
        }
        return aramaParamtreler;
    }

    public static boolean benzinliManuelVites(VitesEnum vites, String yakit) {
        return vites.equals(VitesEnum.Manuel) && benzinLpg().contains(yakit);
    }

    public static String benzinLpg() {
        return YakitEnum.Benzin.getValue();
    }

    public static String dizel() {
        return YakitEnum.Dizel.getValue();
    }

    public static boolean dizelOtomatikVites(String vites, String yakit) {
        return VitesEnum.Otomatik.getValue().contains(vites) && yakit.equals(dizel());
    }


}
