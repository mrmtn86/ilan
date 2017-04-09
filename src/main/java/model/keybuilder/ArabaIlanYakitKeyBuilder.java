package model.keybuilder;

import model.ArabaIlan;

/**
 * Created by mac on 09/04/17.
 */
public class ArabaIlanYakitKeyBuilder implements ArabaIlanKeyBuilder {
    @Override
    public String getKey(ArabaIlan arabaIlan) {
        return arabaIlan.yakit;
    }

    @Override
    public void setPuan(ArabaIlan arabaIlan, int puan) {
        arabaIlan.yakitPuani = puan;
    }
}
