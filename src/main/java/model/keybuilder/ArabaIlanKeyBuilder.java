package model.keybuilder;

import model.ArabaIlan;

/**
 * Created by mac on 09/04/17.
 */
public interface ArabaIlanKeyBuilder {

    String getKey(ArabaIlan arabaIlan);

    void setPuan(ArabaIlan arabaIlan , int puan);
}
