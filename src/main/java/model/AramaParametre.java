package model;

import entity.ArabaModel;
import parser.html.KimdenEnum;

/**
 * Created by mtn on 31.03.2017.
 */
public class AramaParametre {
    private static final String trPlaka = "&a9620=143038";
    private static final String ilanSayisi = "&pagingSize=50";
    private static final String sort = "&sorting=date_asc";
    private static final String gerigidilecekGun = "&date=3days";

    private  String kimden ;
    public int yil;
    public String vites;
    public String yakit;
    public ArabaModel arabaModel;
    public KimdenEnum satan;
    public int ilanDurum =0;
    public Boolean yayinda;


    public AramaParametre(String vites, String yakit, int yil, ArabaModel arabaModel, KimdenEnum satan) {
        this.vites = vites;
        this.yakit = yakit;
        this.yil = yil;
        this.arabaModel = arabaModel;
        kimden += satan.getValue();
        this.satan=satan;
    }

    public AramaParametre() {

    }

    public String geturlString() {

        String urlresult = arabaModel.url;


        if (yakit != null) {
            urlresult += "/" + yakit;
        }
        if (vites != null) {
            urlresult += "/" + vites;
        }



        String yilQuery = "&a5_min=" + yil + "&a5_max=" + yil;
        urlresult = urlresult + "/"+kimden+ "?"  + ilanSayisi + trPlaka  + yilQuery + sort ;

        //urlresult += gerigidilecekGun;

        return urlresult;

    }

    @Override
    public String toString() {
        return "AramaParametre{" +
                "yil=" + yil +
                ", vites='" + vites + '\'' +
                ", yakit='" + yakit + '\'' +
                ", arabaModel=" + arabaModel +
                ", satan='" + satan + '\'' +
                '}';
    }
}
