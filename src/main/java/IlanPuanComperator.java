import java.util.Comparator;

/**
 * Created by mac on 28/02/17.
 */


public class IlanPuanComperator implements Comparator<ArabaIlan> {

    @Override
    public int compare(ArabaIlan o1, ArabaIlan o2) {

        if (o1.model == o2.model) {

            return o1.ilanPuani.compareTo(o2.ilanPuani);
        } else {
            return new Integer(o1.model).compareTo(o2.model);
        }

    }
}