package model;

/**
 * Created by mac on 09/04/17.
 */
public class ArabaIlanIlanNoKeyBuilder implements ArabaIlanKeyBuilder {
    @Override
    public Object getKey(ArabaIlan arabaIlan) {
        return arabaIlan.ilanNo;
    }
}
