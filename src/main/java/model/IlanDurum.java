package model;

/**
 * Created by mtn on 31.03.2017.
 */
public enum IlanDurum {
    Uygun(1), MaxFiyatiAsiyor(2), KaraLisetede(3), PuanUygunDegil(4),
    AciklamadaUygunsuzlukVar(5), Hasarli(7), KaraListede(9);

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
