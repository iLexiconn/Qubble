package net.ilexiconn.qubble.server.color;

public class ColorMode {
    public static final ColorMode DARK = ColorMode.create("dark", 0xFF212121, 0xFF363636, 0xFF464646, 0xFF212121, 0xFF1F1F1F, 0xFFFFFFFF);
    public static final ColorMode LIGHT = ColorMode.create("light", 0xFFCDCDCD, 0xFFACACAC, 0xFFECECEC, 0xFFCDCDCD, 0xFFC2C2C2, 0xFF000000);

    private String name;
    private int primaryColor;
    private int secondaryColor;
    private int tertiaryColor;
    private int primarySubcolor;
    private int secondarySubcolor;
    private int textColor;

    private ColorMode(String name, int primaryColor, int secondaryColor, int tertiaryColor, int primarySubcolor, int secondarySubcolor, int textColor) {
        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.tertiaryColor = tertiaryColor;
        this.primarySubcolor = primarySubcolor;
        this.secondarySubcolor = secondarySubcolor;
        this.textColor = textColor;
    }

    public static ColorMode create(String name, int primaryColor, int secondaryColor, int tertiaryColor, int primarySubcolor, int secondarySubcolor, int textColor) {
        return new ColorMode(name, primaryColor, secondaryColor, tertiaryColor, primarySubcolor, secondarySubcolor, textColor);
    }

    public String getName() {
        return this.name;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getSecondaryColor() {
        return secondaryColor;
    }

    public int getTertiaryColor() {
        return tertiaryColor;
    }

    public int getPrimarySubcolor() {
        return primarySubcolor;
    }

    public int getSecondarySubcolor() {
        return secondarySubcolor;
    }

    public int getTextColor() {
        return textColor;
    }
}
