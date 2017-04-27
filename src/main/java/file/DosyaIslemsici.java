package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by mac on 27/04/17.
 */
public class DosyaIslemsici {

    public static List<Integer> uygunsuzListeGetir(String dosyaAdi) {
        List<Integer> uygunsuzList=new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(dosyaAdi+".txt"))) {

            stream.forEach(s -> {
                Integer ilanNo = Integer.valueOf(s);
                uygunsuzList.add(ilanNo);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return uygunsuzList;
    }
}
