package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mac on 10/04/17.
 */
public class DateUtil {

    public static final String DB_DATE = "yyyy.MM.dd";

    public static String htmlDateTodbDate(String dateHtml) {
        SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy", new Locale("tr"));
        try {
            Date date = df.parse(dateHtml);
            dateHtml = new SimpleDateFormat(DB_DATE).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateHtml;
    }

    public static String nowDbDateTime() {
        return new SimpleDateFormat(DB_DATE + " - hh:mm:ss").format(new Date());
    }
}
