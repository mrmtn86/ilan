import model.ArabaIlan;
import model.AramaParametre;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import parser.html.HtmlParser;

import java.io.File;
import java.util.List;


/**
 * Created by mtn on 6.04.2017.
 */
public class HtmlParserTest {
    @Test
    public void sayfadanGetir() throws Exception {

        HtmlParser htmlParser = new HtmlParser();

        AramaParametre aramaParametre = new AramaParametre();
        htmlParser.sayfadanGetir(aramaParametre);
    }

    @org.junit.Test
    public void ilanlariGetirmeTesti() throws Exception {

        File input = new File("src/test/data/html/civic2007Result.html");
        Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

        HtmlParser parser = new HtmlParser();

        List<ArabaIlan> arabaIlans = parser.arabaIlanlariGetir(doc);

        Assert.assertEquals(50, arabaIlans.size());
    }


}
