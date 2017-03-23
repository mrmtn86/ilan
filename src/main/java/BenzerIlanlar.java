import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 28/02/17.
 */
public class BenzerIlanlar {
    List<ArabaIlan> benzerIlanlar = new ArrayList<>();
    int ortalamaKm;
    int ortalamaFiyat;

    public void IlanEkle(ArabaIlan arabaIlan) {
        benzerIlanlar.add(arabaIlan);
        ortalamaKm += (arabaIlan.km - ortalamaKm) / benzerIlanlar.size();
        ortalamaFiyat += (arabaIlan.fiyat - ortalamaFiyat) / benzerIlanlar.size();
    }
}
