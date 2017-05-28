import config.LogLevelContainer;
import db.Repo;
import entity.ArabaModel;
import model.ArabaIlan;
import model.AramaParametre;
import parser.html.AramaParametreBuilder;
import parser.html.HtmlParser;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        logger.setLevel(LogLevelContainer.LogLevel);

        Repo repo = new Repo();
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
