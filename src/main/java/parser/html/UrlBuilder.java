package parser.html;

import entity.ArabaModel;
import model.Url;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtn on 31.03.2017.
 */
public class UrlBuilder {

    static boolean MANUEL_BENZIN_PAS_GEC = false;
    private static String[] yakitSecenek = {"dizel", "benzin-lpg,benzin"};
    private static String[] vitesSecenek = {"otomatik,yari-otomatik", "manuel"};


    private static final List<Url> urls = new ArrayList<>();



    public static List<Url> getUrls(String modelUrl ,int yil) {
        for (String vites : vitesSecenek) {

            for (String yakit : yakitSecenek) {

                //benzinli manuel olanlarla ilgilenme
                if (MANUEL_BENZIN_PAS_GEC && vites.equals(vitesSecenek[1]) && yakit.equals(yakitSecenek[1])) {
                    continue;
                }

                Url url = new Url(vites, yakit , yil , modelUrl);

                urls.add(url);
            }
        }
        return urls;
    }


}
