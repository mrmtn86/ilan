package parser.html;

import entity.ArabaModel;
import model.AramaParametre;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtn on 31.03.2017.
 */
public class AramaParametreBuilder {

    public static String[] yakitSecenek = {"dizel", "benzin-lpg,benzin"};
    private static boolean MANUEL_BENZIN_PAS_GEC = false;

    public static List<AramaParametre> parametreleriGetir(ArabaModel arabaModel, int yil) {

        List<AramaParametre> aramaParamtreler = new ArrayList<>();
        for (VitesEnum vites : VitesEnum.values()) {

            for (String yakit : yakitSecenek) {

                for (KimdenEnum satan : KimdenEnum.values()) {
                    //benzinli manuel olanlarla ilgilenme
                    if (MANUEL_BENZIN_PAS_GEC && benzinliManuelVites(vites, yakit)) {
                        continue;
                    }

                    AramaParametre aramaParametre = new AramaParametre(vites, yakit, yil, arabaModel, satan);

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
        return yakitSecenek[1];
    }

    public static String dizel() {
        return yakitSecenek[0];
    }

    public static boolean dizelOtomatikVites(String vites, String yakit) {
        return VitesEnum.Otomatik.getValue().contains(vites) && yakit.equals(dizel());
    }


}
