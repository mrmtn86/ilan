package model.keybuilder;

import model.ArabaIlan;

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
    public String getKey(ArabaIlan arabaIlan) {

        String arabaninEklenecegiPaket = "diger";
        for (String paket : paketler) {
            if (arabaIlan.paket.contains(paket)) {
                arabaninEklenecegiPaket = paket;
                break;
            }
        }
        return arabaninEklenecegiPaket;
    }

    @Override
    public void setKeyPuan(ArabaIlan arabaIlan, int puan) {
        arabaIlan.paketPuani = puan;
    }

}
