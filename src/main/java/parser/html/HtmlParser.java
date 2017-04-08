package parser.html;

import config.LogLevelContainer;
import model.ArabaIlan;
import model.IlanDurum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
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

    private static Logger logger = Logger.getLogger(HtmlParser.class.getName());
    private static String[] kusurluAciklamlar = {"ağır hasar kaydı var",
            "agir hasar kaydı mevcut", "" +
            "ÇEKME BELGELİ",
            "HASARLI AL",
            "Ağır Hasar Kayıtlıdır",
            "ağır hasar kayıtlıdır",
            "pert kayıtlı  aldım"};


    public HtmlParser() {
        logger.setLevel(LogLevelContainer.LogLevel);
    }

    private static Document httpGet(String url) throws IOException {
        String urlAll = "https://www.sahibinden.com/" + url;
        logger.log(Level.INFO, "url get :  {0}", urlAll);
        return Jsoup.connect(urlAll).get();

    }

    private static IlanDurum aciklamaTaramasiTemiz(Document doc) {

        for (String aciklama : kusurluAciklamlar) {
            if (aciklamadaVarmi(doc, aciklama)) {
                return IlanDurum.AciklamadaUygunsuzlukVar;
            }
        }


        return IlanDurum.Uygun;
    }

    private static boolean aciklamadaVarmi(Document doc, String aciklama) {
        Elements select = doc.select("#classifiedDescription");
        Elements select1 = select.select(":contains(" + aciklama.toLowerCase(new Locale("tr")) + ")");
        if (select1.size() > 0) {
            return true;
        }
        select1 = select.select(":contains(" + aciklama.toUpperCase(new Locale("tr")) + ")");

        return select1.size() > 0;

    }

    private static ArabaIlan getArabaIlan(Element element) {

        Elements select = element.select(".searchResultsAttributeValue");

        if (select == null || select.size() == 0) {
            return null;
        }

        String ilanNo = element.attributes().iterator().next().getValue();


        ;
        String yilElement = select.first().text();
        String kmElement = select.get(1).text().replace(".", "");
        String fiyatStr = element.select(".searchResultsPriceValue").text().replace(".", "").replace("TL", "").replace(" ", "");
        String ilanUrl = element.select("a").first().attr("href");
        String paket = element.select(".searchResultsTagAttributeValue").first().text();

        int yil = Integer.parseInt(yilElement);
        int km = Integer.parseInt(kmElement);

        int fiyatTmp = Integer.parseInt(fiyatStr);
        int fiyat = fiyatTmp;
        ilanUrl = ilanUrl.substring(1, ilanUrl.length());

        String tarihStr = element.select(".searchResultsDateValue").text();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy", new Locale("tr"));
        try {
            Date date = df.parse(tarihStr);
            tarihStr = new SimpleDateFormat("yyyy.MM.dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String baslik = element.select(".searchResultsTitleValue").text();
        String ilIlce = element.select(".searchResultsLocationValue").text();

        int ilanNoInt = Integer.parseInt(ilanNo);


        ArabaIlan arabaIlan = new ArabaIlan(yil, fiyat, km, tarihStr, baslik, ilanUrl, ilanNoInt, paket);
        arabaIlan.ilIlce = ilIlce;

        return arabaIlan;
    }

    public static Elements csstenSec(Document doc, String cssQuery) {
        return doc.select("." + cssQuery);
    }

    public List<ArabaIlan> arabaIlanlariGetir(String url) {
        List<ArabaIlan> ilanlar = new ArrayList<>();
        try {

            Document doc = httpGet(url);
            Elements arabalar = csstenSec(doc, "searchResultsItem");


            for (Element element : arabalar) {
                try {

                    ArabaIlan araba = getArabaIlan(element);
                    if ((araba != null)) {
                        ilanlar.add(araba);

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }

        return ilanlar;
    }

    public IlanDurum aciklamaTemiz(ArabaIlan arabaIlan) {

        Document document = null;
        try {
            document = httpGet(arabaIlan.ilanUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arabaIlan.setDurum(aciklamaTaramasiTemiz(document));

    }
}
