package model;

/**
 * Created by mtn on 31.03.2017.
 */
public class Url {
    public String vites;
    public String yakit;

    private static String sahibinden = "a706=32474";
    private static String trPlaka = "&a9620=143038";
    private static String ilanSayisi = "&pagingSize=50";


    public Url(String vites, String yakit) {
        this.vites = vites;
        this.yakit = yakit;
    }

    public String geturlString() {
        return yakit + vites + "?" + sahibinden + ilanSayisi + trPlaka;
    }
}
