import com.mongodb.client.MongoDatabase;
import db.DbContainer;
import parser.html.HtmlParser;
import service.MainService;

import java.io.IOException;
import java.util.logging.Logger;


public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

        MongoDatabase db = DbContainer.getCloudDb();

        MainService mainService = new MainService(db);

        HtmlParser htmlParser = new HtmlParser();
        mainService.ilanlariSayfadanGuncelle(htmlParser);
    }
}
