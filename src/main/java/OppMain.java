import db.DbContainer;
import db.Repo;

import java.util.logging.Logger;

/**
 * Created by mac on 18/04/17.
 */
public class OppMain {

    private static Logger logger = Logger.getLogger(OppMain.class.getName());

    public static void main(String[] args) {
        Repo repo = new Repo(DbContainer.getCloudDb());
        repo.butunIlanlariyayindaYap();
    }
}
