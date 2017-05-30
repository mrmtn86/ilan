package parser.html;

/**
 * Created by mac on 30/05/17.
 */
public enum VitesEnum {
    Otomatik("otomatik,yari-otomatik"), Manuel("manuel");
    private String value;

    VitesEnum(String s) {

        value = s;
    }

    public String getValue() {
        return value;
    }

    public static VitesEnum getEnum(String str) {


        for (VitesEnum vitesEnum : VitesEnum.values()) {
            if (vitesEnum.getValue().contains(str)) {
                return vitesEnum;
            }
        }


        return VitesEnum.valueOf(str);
    }

}
