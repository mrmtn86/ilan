/**
 * Created by mac on 27/02/17.
 */
public class ArabaIlan {
    public int model;
    public int fiyat;
    public int km;
    public String ilanTarhi;
    public String baslik;
    public String ilanUrl;
    public int ilanNo;
    public Integer ilanPuani;


    public ArabaIlan(int model, int fiyat, int km, String ilanTarhi, String baslik, String ilanUrl, int ilanNoInt) {
        this.model = model;
        this.fiyat = fiyat;
        this.km = km;
        this.ilanTarhi = ilanTarhi;
        this.baslik = baslik;
        this.ilanUrl = ilanUrl;
        ilanNo = ilanNoInt;
    }

    @Override
    public String toString() {
        return "ArabaIlan{" +
                "puan=" + ilanPuani +
                "model=" + model +
                ", fiyat=" + fiyat +
                ", km=" + km +
                ", ilanTarhi='" + ilanTarhi + '\'' +
                ", baslik='" + baslik + '\'' +
                ", url='https://www.sahibinden.com/" + ilanUrl + '\'' +
                '}';
    }
}
