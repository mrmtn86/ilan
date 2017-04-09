package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;

/**
 * Created by mac on 27/02/17.
 */
public class ArabaIlan {
    @JsonIgnore
    public ObjectId dbId;
    public String modelId;
    public int yil;
    public int fiyat;
    public int km;
    public String ilanTarhi;
    public String baslik;
    public String ilanUrl;
    public int ilanNo;
    public String paket;
    public int kmPuani;
    public int fiyatPuani;
    public Integer ilanPuani;
    public String yakit;
    public String vites;
    public String ilIlce;
    public int ilandurum;
    public String kimden;

    @JsonIgnore
    private IlanDurum durum;
    public String aciklama;

    public ArabaIlan(int yil, int fiyat, int km, String ilanTarhi, String baslik, String ilanUrl, int ilanNoInt, String paket) {
        this.yil = yil;
        this.fiyat = fiyat;
        this.km = km;
        this.ilanTarhi = ilanTarhi;
        this.baslik = baslik;
        this.ilanUrl = ilanUrl;
        this.ilanNo = ilanNoInt;
        this.paket = paket;
    }

    public int getIlandurum() {
        return ilandurum;
    }

    public void setIlandurum(int ilandurum) {
        this.ilandurum = ilandurum;
        if (durum != null && durum.getIndex() != ilandurum) {
            setDurum(IlanDurum.getEnum(ilandurum));
        }
        ;
    }

    public IlanDurum getDurum() {
        return durum;
    }

    public IlanDurum setDurum(IlanDurum durum) {
        this.durum = durum;
        if (durum != null && durum.getIndex() != ilandurum) {
            setIlandurum(durum.getIndex());
        }

        return this.durum ;
    }

    @Override
    public String toString() {
        return "yil.ArabaIlan{" +
                "ilanNo=" + ilanNo +
                "puan=" + ilanPuani +
                "yil=" + yil +
                ", fiyat=" + fiyat +
                ", km=" + km +
                ", ilanTarhi='" + ilanTarhi + '\'' +
                ", baslik='" + baslik + '\'' +
                ", url='https://www.sahibinden.com/" + ilanUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArabaIlan arabaIlan = (ArabaIlan) o;

        if (fiyat != arabaIlan.fiyat) return false;
        return dbId != null ? dbId.equals(arabaIlan.dbId) : arabaIlan.dbId == null;

    }

    @Override
    public int hashCode() {
        int result = dbId != null ? dbId.hashCode() : 0;
        result = 31 * result + fiyat;
        return result;
    }
}
