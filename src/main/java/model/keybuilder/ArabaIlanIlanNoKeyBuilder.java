package model.keybuilder;

import model.ArabaIlan;

/**
 * Created by mac on 09/04/17.
 */
public class ArabaIlanIlanNoKeyBuilder implements ArabaIlanKeyBuilder {
    @Override
    public String getKey(ArabaIlan arabaIlan) {
        return String.valueOf(arabaIlan.ilanNo);
    }
}
