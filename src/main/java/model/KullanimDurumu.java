package model;

/**
 * Created by mtn on 4.04.2017.
 */
public enum KullanimDurumu {
    Aktif(1), Pasif(2);

    private int index;

    KullanimDurumu(int index) {

        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static KullanimDurumu getEnum(int index) {
        for (KullanimDurumu enumItr : KullanimDurumu.values()) {

            if (enumItr.index == index) return enumItr;
        }
        return null;
    }

}
