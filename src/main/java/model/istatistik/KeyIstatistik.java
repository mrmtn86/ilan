package model.istatistik;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 10/04/17.
 */
public class KeyIstatistik {

    public String key;
    public List<OrtalamaIsatatistik> ortalamaIsatatistikList = new ArrayList<>();

    public KeyIstatistik(String key) {
        this.key = key;
    }
}
