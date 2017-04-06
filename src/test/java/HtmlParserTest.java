import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import parser.html.HtmlParser;


/**
 * Created by mtn on 6.04.2017.
 */
public class HtmlParserTest {

    @org.junit.Test
    public void ilanlariGetirmeTesti() throws Exception {

        String arabaılanhtml = "<tr data-id='419536452' class='searchResultsItem'><b>example</b> </tr>";
//        String arabaılanhtml =
//                "<tr data-id='419536452' class='searchResultsItem     '>\n" +
//                        "<td class='searchResultsLargeThumbnail'>\n" +
//                        "<a href='/ilan/vasita-otomobil-volkswagen-ilk-sahibinden-orjinal-sorunsuz-419536452/detay'>\n" +
//                        "\n" +
//                        "<img class='searchResultThumbnailPlaceholder otherNoImage' src='https://s0.shbdn.com/assets/images/iconHasMegaPhoto:1d086aab554fd92d49d3762a0542888a.png' alt='İlk sahibinden, orjinal, sorunsuz. #419536452' title='Fotoğraflı ilan'>\n" +
//                        "</a></td>\n" +
//                        "<td class='searchResultsTagAttributeValue'>\n" +
//                        "        1.2 TDi BlueMotion</td>\n" +
//                        "<td class='searchResultsTitleValue '>\n" +
//                        "<input id='favoriteClassifiedsVisibility' value='false' type='hidden'>\n" +
//                        "<div class='action-wrapper' data-classified-id='419536452'>\n" +
//                        "</div>\n" +
//                        "<img class='titleIcon' src='https://s0.shbdn.com/assets/images/iconNew:c9b443de96056beb84b4cdc03ca5051c.png' alt='Yeni İlan' title='Yeni İlan' style='visibility: visible;'>\n" +
//                        "<a class='classifiedTitle' href='/ilan/vasita-otomobil-volkswagen-ilk-sahibinden-orjinal-sorunsuz-419536452/detay'>\n" +
//                        "        İlk sahibinden, orjinal, sorunsuz.</a>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "<td class='searchResultsAttributeValue'>\n" +
//                        "        2011</td>\n" +
//                        "<td class='searchResultsAttributeValue'>\n" +
//                        "        123.000</td>\n" +
//                        "<td class='searchResultsAttributeValue'>\n" +
//                        "        Beyaz</td>\n" +
//                        "<td class='searchResultsPriceValue'>\n" +
//                        "<div> 44.000 TL</div></td>\n" +
//                        "<td class='searchResultsDateValue'>\n" +
//                        "<span>06 Nisan</span>\n" +
//                        "<br>\n" +
//                        "<span>2017</span>\n" +
//                        "</td>\n" +
//                        "<td class='searchResultsLocationValue'>\n" +
//                        "        Ankara<br>Çankaya</td>\n" +
//                        "<td class='ignore-me'>\n" +
//                        "<a href='#' class='mark-as-ignored' title='Bu ilanla ilgilenmiyorum, gizle.'></a>\n" +
//                        "<a href='#' class='mark-as-not-ignored disable'>\n" +
//                        "        Göster</a>\n" +
//                        "</td>\n" +
//                        "</tr>\n" + "" +
//                        //ilan 2
//                        "<tr data-id='402473357' class='searchResultsItem     '>\n" +
//                        "    <td class='searchResultsLargeThumbnail'>\n" +
//                        "            <a href='/ilan/vasita-otomobil-volkswagen-stock-gti-sahibinden-402473357/detay'>\n" +
//                        "\n" +
//                        "    <img src='https://image5.sahibinden.com/photos/47/33/57/thmb_4024733575uo.jpg' alt='STOCK GTİ Sahibinden #402473357' title='STOCK GTİ Sahibinden'>\n" +
//                        "    </a></td>\n" +
//                        "    <td class='searchResultsTagAttributeValue'>\n" +
//                        "                        1.4 TSi GTi</td>\n" +
//                        "                <td class='searchResultsTitleValue '>\n" +
//                        "                    <input id='favoriteClassifiedsVisibility' value='false' type='hidden'>\n" +
//                        "<div class='action-wrapper' data-classified-id='402473357'>\n" +
//                        "                        </div>\n" +
//                        "                <a class='classifiedTitle' href='/ilan/vasita-otomobil-volkswagen-stock-gti-sahibinden-402473357/detay'>\n" +
//                        "    STOCK GTİ Sahibinden</a>\n" +
//                        "\n" +
//                        "</td>\n" +
//                        "            <td class='searchResultsAttributeValue'>\n" +
//                        "                    2011</td>\n" +
//                        "            <td class='searchResultsAttributeValue'>\n" +
//                        "                    79.000</td>\n" +
//                        "            <td class='searchResultsAttributeValue'>\n" +
//                        "                    Beyaz</td>\n" +
//                        "            <td class='searchResultsPriceValue'>\n" +
//                        "                        <div> 55.750 TL</div></td>\n" +
//                        "                <td class='searchResultsDateValue'>\n" +
//                        "                        <span>06 Nisan</span>\n" +
//                        "                        <br>\n" +
//                        "                        <span>2017</span>\n" +
//                        "                    </td>\n" +
//                        "                <td class='searchResultsLocationValue'>\n" +
//                        "                        İstanbul<br>Bakırköy</td>\n" +
//                        "                <td class='ignore-me'>\n" +
//                        "    <a href='#' class='mark-as-ignored' title='Bu ilanla ilgilenmiyorum, gizle.'></a>\n" +
//                        "    <a href='#' class='mark-as-not-ignored disable'>\n" +
//                        "        Göster</a>\n" +
//                        "</td>\n" +
//                        "</tr>";
        Document doc = Jsoup.parse(arabaılanhtml);

        doc.select("searchResultsItem");
        HtmlParser htmlParser = new HtmlParser();
        Elements arabalar = HtmlParser.csstenSec(doc, "searchResultsItem");

        //Assert.assertEquals(arabalar.size(), 2);

    }




}
