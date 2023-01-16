package sky.pro.skyprotermwork3.model;

public enum Color {

    BLACK("Черный"),
    WHITE("Белый"),
    GREY("Серыйй"),
    DARK_BLUE("Тёмно-синий"),
    BROWN("Коричневый");
    private final String colorName;

    Color(String colorName) {
        this.colorName = colorName;
    }
}
