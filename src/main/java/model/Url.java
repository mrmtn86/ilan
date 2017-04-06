package model;

/**
 * Created by mtn on 31.03.2017.
 */
public class Url {
    public String vites;
    public String yakit;
    public final int yil;
    private String modelUrl;

    private static final String sahibinden = "a706=32474";
    private static final String trPlaka = "&a9620=143038";
    private static final String ilanSayisi = "&pagingSize=50";
    private static final String sort = "&sorting=date_desc";
    private static final String gerigidilecekGun = "&date=3days";


    public Url(String vites, String yakit, int yil, String modelUrl) {
        this.vites = vites;
        this.yakit = yakit;
        this.yil = yil;
        this.modelUrl = modelUrl;
    }

    public String geturlString() {

        String urlresult = modelUrl ;


        if (vites != null) {
            urlresult += "/" + vites ;
        }
        if (yakit != null) {
            urlresult += "/" + yakit ;
        }

        String yilQuery = "&a5_min=" + yil + "&a5_max=" + yil;
        return urlresult  + "?" + sahibinden + ilanSayisi + trPlaka + sort + gerigidilecekGun + yilQuery;
    }
}
