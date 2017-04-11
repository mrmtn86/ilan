package model.istatistik;

import java.util.Date;

/**
 * Created by mac on 10/04/17.
 */
public class OrtalamaIsatatistik {
    public int ortKm;
    public int ortFiyat;
    public Date istTarihi;

    public OrtalamaIsatatistik(int ortKm, int ortFiyat) {
        this.ortKm = ortKm;
        this.ortFiyat = ortFiyat;
        this.istTarihi = new Date();
    }
}
