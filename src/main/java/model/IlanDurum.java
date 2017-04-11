package model;

/**
 * Created by mtn on 31.03.2017.
 */
public enum IlanDurum {
    Uygun(1), KaraLisetede(2), AciklamadaUygunsuzlukVar(3), Hasarli(4), Istenmiyor(5), MaxFiyatiAsiyor(6) , PuanUygunDegil(7),YanlisBilgi(8) ;

    private int index;


    IlanDurum(int index) {

        this.index = index;
    }

    public static IlanDurum getEnum(int index) {
        for (IlanDurum ilanDurum : IlanDurum.values()) {

            if (ilanDurum.index == index) {
                return ilanDurum;
            }
        }

        return null;
    }

    public int getIndex() {
        return index;
    }

}
