package ru.study.chain;

public enum BetColor {
    RED("Красный", "#cf4147", 2),
    BLACK("Чёрный", "#303540", 2),
    GREEN("Зелёный", "#329565", 5);

    private final String title;
    private final String hex;
    private final int multiplier;

    BetColor(String title, String hex, int multiplier) {
        this.title = title;
        this.hex = hex;
        this.multiplier = multiplier;
    }

    public String getTitle() { return title; }
    public String getHex() { return hex; }
    public int getMultiplier() { return multiplier; }
}
