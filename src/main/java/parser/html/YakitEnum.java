package parser.html;

/**
 * Created by mtn on 4.06.2017.
 */
public enum YakitEnum {
    Dizel("dizel") , Benzin("benzin-lpg,benzin");

    private String value;

    YakitEnum(String s) {

        value = s;
    }

    public String getValue() {
        return value;
    }

    public static YakitEnum getEnum(String str) {


        for (YakitEnum yakitEnum : YakitEnum.values()) {
            if (yakitEnum.getValue().contains(str)) {
                return yakitEnum;
            }
        }


        return YakitEnum.valueOf(str);
    }
}
