package sky.pro.skyprotermwork3.model;

public enum Size {
    SIZE_6_8("6-8"),
    SIZE_8_10("8-10"),
    SIZE_10_12("10-12"),
    SIZE_12_14("12-14"),
    SIZE_14_16("14-16"),
    SIZE_18_20("16-18"),
    SIZE_22_24("18-20"),
    SIZE_23("23"),
    SIZE_25("25"),
    SIZE_27("27"),
    SIZE_29("29"),
    SIZE_31("31");
    private String sizeStr;

    Size(String sizeStr) {
        this.sizeStr = sizeStr;
    }
}
