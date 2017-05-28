package parser.html;

import config.LogLevelContainer;
import model.ArabaIlan;
import model.AramaParametre;
import model.IlanDurum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.DateUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 23/03/17.
 */
public class HtmlParser {

    public static final String SAHBINDEN_BASE_URL = "https://www.sahibinden.com/";
    private static Logger logger = Logger.getLogger(HtmlParser.class.getName());



    public HtmlParser() {
        logger.setLevel(LogLevelContainer.LogLevel);
    }

    private static Document httpGet(String url) throws IOException {
        String urlAll = SAHBINDEN_BASE_URL + url;
        logger.log(Level.CONFIG, "url get :  {0}", urlAll);
        return Jsoup.connect(urlAll).get();

    }

    private static ArabaIlan getArabaIlan(Element element) {
        String ilanNo = null;
        try {

        Elements select = element.select(".searchResultsAttributeValue");

        if (select == null || select.size() == 0) {
            return null;
        }

          ilanNo = element.attributes().iterator().next().getValue();


        String yilElement = select.first().text();
        String kmElement = select.get(1).text().replace(".", "");
        String fiyatStr = element.select(".searchResultsPriceValue").text().replace(".", "").replace("TL", "").replace(" ", "");
        String ilanUrl = element.select("a").first().attr("href");
        String paket = element.select(".searchResultsTagAttributeValue").first().text();

        int yil = Integer.parseInt(yilElement);
        int km = Integer.parseInt(kmElement);

        fiyatStr=fiyatStr.replace("$","").replace("€","");

        int fiyatTmp = Integer.parseInt(fiyatStr.split(",")[0]);
        int fiyat = fiyatTmp;
        ilanUrl = ilanUrl.substring(1, ilanUrl.length());

        String tarihStr = element.select(".searchResultsDateValue").text();

        tarihStr= DateUtil.htmlDateTodbDate(tarihStr);
        String baslik = element.select(".searchResultsTitleValue").text();
        baslik = baslik.replace("Favorilerime Ekle Favorilerimde Karşılaştır " , "");
        String ilIlce = element.select(".searchResultsLocationValue").text();

        int ilanNoInt = Integer.parseInt(ilanNo);


        ArabaIlan arabaIlan = new ArabaIlan(yil, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt, paket);
        arabaIlan.ilIlce = ilIlce;

        return arabaIlan;
        } catch (Exception ex) {

            if (ilanNo!= null){
                logger.log(Level.WARNING , "{0} nolu ilanda hata" , ilanNo);
            }

            ex.printStackTrace();

        }
        return null;
    }

    public static Elements csstenSec(Document doc, String cssQuery) {
        return doc.select("." + cssQuery);
    }

    public List<ArabaIlan> sayfadanGetir(AramaParametre aramaParametre) {



        String urlResult = aramaParametre.geturlString();

        List<ArabaIlan> arabaIlanListSonuc = new ArrayList<>();

        for (int i = 0; i <= 1000; i = i + 50) {

            if (i >= 1000)
                logger.warning(aramaParametre + " icin 1000 ilan gecildi deger  " + i);

            String ofsetValue = "";
            if (i > 0) {
                ofsetValue = "&pagingOffset=" + i;
            }

            List<ArabaIlan> arabaIlanList = arabaIlanlariGetir(urlResult + ofsetValue);

            arabaIlanListSonuc.addAll(arabaIlanList);

            if (arabaIlanList.size() == 0 || arabaIlanList.size() < 50) {
                break;
            }
        }
        return arabaIlanListSonuc;
    }

    public List<ArabaIlan> arabaIlanlariGetir(String url) {
        List<ArabaIlan> ilanlar = new ArrayList<>();
        try {

            Document doc = httpGet(url);
            Elements arabalar = csstenSec(doc, "searchResultsItem");


            for (Element element : arabalar) {


                    ArabaIlan araba = getArabaIlan(element);
                    if ((araba != null)) {
                        ilanlar.add(araba);

                    }

            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }

        return ilanlar;
    }


    public String aciklamayiGetir(ArabaIlan arabaIlan) {

        Document document = null;
        try {
            document = httpGet(arabaIlan.ilanUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return document.select("#classifiedDescription").text();




    }
}
