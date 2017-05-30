package service;

import com.mongodb.client.MongoDatabase;
import config.LogLevelContainer;
import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.AramaParametre;
import parser.html.AramaParametreBuilder;
import parser.html.HtmlParser;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 30/05/17.
 */
public class MainService {


    private static Logger logger = Logger.getLogger(MainService.class.getName());
    private Repo repo;

    public MainService(MongoDatabase db) {
        this.repo = new Repo(db);
    }

    public void ilanlariSayfadanGuncelle() {

        logger.setLevel(LogLevelContainer.LogLevel);

        HtmlParser htmlParser = new HtmlParser();
        List<ArabaModel> modeller = repo.modelleriGetir();

        int guncellenenAracSayisi = 0;
        for (ArabaModel arabaModel : modeller) {

            for (int yilParam = arabaModel.baslangicYili; yilParam <= arabaModel.bitisYili; yilParam++) {

                List<AramaParametre> aramaParametres = AramaParametreBuilder.parametreleriGetir(arabaModel, yilParam);

                for (AramaParametre aramaParametreItr : aramaParametres) {
                    List<ArabaIlan> arabaIlanList = htmlParser.sayfadanGetir(aramaParametreItr);
                    guncellenenAracSayisi += repo.dbguncelle(aramaParametreItr, arabaIlanList);
                }
            }
        }
        logger.log(Level.INFO, " toplam : {0}", new Object[]{guncellenenAracSayisi});
    }
}
