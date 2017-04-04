package parser.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ArabaIlan;

/**
 * Created by mac on 04/04/17.
 */
public class JsonParser {



    public static String toJson(Object o) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
