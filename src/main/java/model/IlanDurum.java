package model;

/**
 * Created by mtn on 31.03.2017.
 */
public enum IlanDurum {
    Uygun(1, true),
    KaraLisetede(2, false),
    AciklamadaUygunsuzlukVar(3, false),
    Hasarli(4, false), Istenmiyor(5, true),
    MaxFiyatiAsiyor(6, true),
    PuanUygunDegil(7, true),
    YanlisBilgi(8, false),
    KmPuanUygunDegil(9, false),
    BosAraba(10, true),
    Galeriden(11, true);

    public boolean ortalamadaKullan;
    private int index;


    IlanDurum(int index, boolean ortalamadaKullan) {

        this.index = index;
        this.ortalamadaKullan = ortalamadaKullan;
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
