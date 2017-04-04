import com.github.fakemongo.Fongo;
import com.mongodb.*;
import entity.ArabaModel;
import model.ArabaIlan;
import org.bson.types.ObjectId;
import org.junit.Assert;
import parser.json.JsonParser;

import java.util.Iterator;

/**
 * Created by mac on 04/04/17.
 */
public class MainTest {
    @org.junit.Test
    public void main() throws Exception {


        Fongo fongo = new Fongo("mongo server 1");

        DB db = fongo.getDB("mydb");
        DBCollection modelCollection = db.getCollection("modelCollection");
        DBCollection ilanCollection = db.getCollection("ilanCollection");


        ArabaModel arabaModel = new ArabaModel("arabamodel", "ururll1", new ObjectId());
        BasicDBObject basicDBObject = new BasicDBObject("ed", arabaModel);


        modelCollection.save(basicDBObject);


        Iterator<DBObject> iterator = modelCollection.find().iterator();

        DBObject dbObject = iterator.next();

        ObjectId modelId = (ObjectId) dbObject.get("_id");

        ArabaIlan arabaIlan = new ArabaIlan(2, 1, 2, "23", "32", "", 2);

        arabaIlan.modelId = modelId.toString();

        DBObject asd =  BasicDBObject.parse(JsonParser.toJson(arabaIlan));
        ilanCollection.save(asd);

        DBCursor dbObjects = ilanCollection.find();


        DBObject dbObjectilan = dbObjects.iterator().next();

        ObjectId ilanId = (ObjectId) dbObjectilan.get("_id");
        for (String key : dbObjectilan.keySet()) {
            System.out.println(key + ":" +dbObjectilan.get(key));
        }


        DBObject queryModel = new BasicDBObject("modelId" , modelId.toString());
        DBCursor dbcursor = ilanCollection.find(queryModel);

        Iterator<DBObject> iteratorModelIden = dbcursor.iterator();



//        Repo repo = new Repo();
//        ObjectId id=new ObjectId();
//        Friend john=new Friend(id,"John");
//        collection.save(john);
//        Friend foundFriend=collection.findOne("{_id:{$oid:#}}",id.toString()).as(Friend.class);


        Assert.assertTrue(true);

    }

    public Mongo mongo() throws Exception {
        return new Fongo("testdb").getMongo();
    }


}