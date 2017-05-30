package entity;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by mac on 21/03/17.
 */
public class ArabaModel {
    public ObjectId id;
    public String ad;
    public String url;
    public List<String> paketler;
    public int baslangicYili;
    public int bitisYili;
    public int kullanimDurumu;


    public ArabaModel(String ad, String url, ObjectId id) {
        this.ad = ad;
        this.url = url;
        this.id = id;
    }


    @Override
    public String toString() {
        return "ArabaModel{" +
                "ad='" + ad + '\'' +
                '}';
    }
}
