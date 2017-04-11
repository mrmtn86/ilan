package model.istatistik;

/**
 * Created by mac on 10/04/17.
 */
public class ModelIstatistik {

    public String modelId;
    public int yil;
    public String key;
    public String istTarihi;
    public int ortKm;
    public int ortFiyat;
    public boolean aktif = true;

    public ModelIstatistik(String modelId, int yil, String key, String istTarihi, int ortKm, int ortFiyat) {
        this.modelId = modelId;
        this.yil = yil;
        this.key = key;
        this.istTarihi = istTarihi;
        this.ortKm = ortKm;
        this.ortFiyat = ortFiyat;
    }
}
