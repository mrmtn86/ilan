package model.keybuilder;

import model.ArabaIlan;

/**
 * Created by mac on 09/04/17.
 */
public class ArabaIlanVitesKeyBuilder implements ArabaIlanKeyBuilder {
    @Override
    public String getKey(ArabaIlan arabaIlan) {
        return arabaIlan.vites;
    }

    @Override
    public void setKeyPuan(ArabaIlan arabaIlan, int puan) {
        arabaIlan.vitesPuani =puan;
    }
}
