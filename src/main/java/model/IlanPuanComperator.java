package model;

import java.util.Comparator;

/**
 * Created by mac on 28/02/17.
 */


public class IlanPuanComperator implements Comparator<ArabaIlan> {

    @Override
    public int compare(ArabaIlan o1, ArabaIlan o2) {
        //if (o1.ilanTarhi.equals(o2.ilanTarhi))
            return new Integer(o1.ilanPuani).compareTo(o2.ilanPuani);
       // return o2.ilanTarhi.compareTo(o1.ilanTarhi);
    }
}