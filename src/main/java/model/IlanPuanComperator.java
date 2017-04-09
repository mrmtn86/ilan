package model;

import java.util.Comparator;

/**
 * Created by mac on 28/02/17.
 */


public class IlanPuanComperator implements Comparator<ArabaIlan> {

    @Override
    public int compare(ArabaIlan o1, ArabaIlan o2) {

        if (o1.yil == o2.yil) {

            return new Integer(o1.ilanPuani).compareTo(o2.ilanPuani);
        } else {
            return new Integer(o1.yil).compareTo(o2.yil);
        }

    }
}