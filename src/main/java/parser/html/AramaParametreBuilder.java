package parser.html;

import entity.ArabaModel;
import model.AramaParametre;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtn on 31.03.2017.
 */
public class AramaParametreBuilder {

    public static final int BITIS_YIL = 2017;
    private static boolean MANUEL_BENZIN_PAS_GEC = false;
    private static String[] yakitSecenek = {"dizel", "benzin-lpg,benzin"};
    private static String[] vitesSecenek = {"otomatik,yari-otomatik", "manuel"};

    public static List<AramaParametre> parametreleriGetir(ArabaModel arabaModel, int yil) {

         List<AramaParametre> aramaPAramtreler = new ArrayList<>();
        for (String vites : vitesSecenek) {

            for (String yakit : yakitSecenek) {

                for (KimdenEnum satan : KimdenEnum.values()) {
                    //benzinli manuel olanlarla ilgilenme
                    if (MANUEL_BENZIN_PAS_GEC && benzinlManuelVites(vites, yakit)) {
                        continue;
                    }

                    AramaParametre aramaParametre = new AramaParametre(vites, yakit, yil, arabaModel , satan);

                    aramaPAramtreler.add(aramaParametre);
                }
            }
        }
        return aramaPAramtreler;
    }

    public static boolean benzinlManuelVites(String vites, String yakit) {
        return vites.equals(vitesSecenek[1]) && yakit.equals(yakitSecenek[1]);
    }


}
