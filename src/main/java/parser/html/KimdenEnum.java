package parser.html;

/**
 * Created by mac on 08/04/17.
 */
public enum KimdenEnum {
    Sahibinden("32474"),Galeriden("32475");

    private String value;

    KimdenEnum(String s) {

        value = s;
    }

    public String getValue() {
        return value;
    }
}
