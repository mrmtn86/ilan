import com.github.fakemongo.Fongo;
import com.mongodb.client.*;
import entity.ArabaModel;
import model.ArabaIlan;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import parser.json.JsonParser;

/**
 * Created by mac on 04/04/17.
 */
public class MainTest {
    @org.junit.Test
    public void main() throws Exception {


        Fongo fongo = new Fongo("mongo server 1");
        MongoDatabase db = fongo.getDatabase("mydb");

        MongoCollection<Document> modelCollection = db.getCollection("modelCollection");
        MongoCollection<Document> ilanCollection = db.getCollection("ilanCollection");


        ArabaModel arabaModel = new ArabaModel("arabamodel", "ururll1", new ObjectId());

        modelCollection.insertOne(Document.parse(JsonParser.toJson(arabaModel)));


        MongoCursor<Document> iterator = modelCollection.find().iterator();

        Document dbObject = iterator.next();

        ObjectId modelId = (ObjectId) dbObject.get("_id");

        ArabaIlan arabaIlan = new ArabaIlan(2, 1, 2, "23", "32", "", 2, "pkt");

        arabaIlan.modelId = modelId.toString();

        Document asd =  Document.parse(JsonParser.toJson(arabaIlan));
        ilanCollection.insertOne(asd);

        FindIterable<Document> dbObjects = ilanCollection.find();


        Document dbObjectilan = dbObjects.iterator().next();

        ObjectId ilanId = (ObjectId) dbObjectilan.get("_id");
        for (String key : dbObjectilan.keySet()) {
            System.out.println(key + ":" +dbObjectilan.get(key));
        }


        Document queryModel = new Document("modelId", modelId.toString());
        MongoIterable<Document> dbcursor = ilanCollection.find(queryModel);

       ObjectId ilanIdResult = (ObjectId) dbcursor.first().get("_id");


        Assert.assertTrue(ilanIdResult.equals(ilanId));

    }




}