package model;

import java.util.List;

/**
 * Created by mac on 09/04/17.
 */
public class ArabaIlanPaketKeyBuilder implements ArabaIlanKeyBuilder {


    private List<String> paketler;

    public ArabaIlanPaketKeyBuilder(List<String> paketler) {
        this.paketler = paketler;
    }

    @Override
    public Object getKey(ArabaIlan arabaIlan) {

        String arabaninEklenecegiPaket = "diger";
        for (String paket : paketler) {
            if (arabaIlan.paket.contains(paket)) {
                arabaninEklenecegiPaket = paket;
                break;
            }
        }
        return arabaninEklenecegiPaket;
    }
}
