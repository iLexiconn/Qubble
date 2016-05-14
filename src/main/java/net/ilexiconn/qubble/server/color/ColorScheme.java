package net.ilexiconn.qubble.server.color;

import net.ilexiconn.llibrary.LLibrary;

import java.util.function.Supplier;

public class ColorScheme {
    public static final ColorScheme DEFAULT = ColorScheme.create(() -> LLibrary.CONFIG.getAccentColor(), () -> LLibrary.CONFIG.getDarkAccentColor());
    public static final ColorScheme TAB_ACTIVE = ColorScheme.create(() -> LLibrary.CONFIG.getPrimaryColor(), () -> LLibrary.CONFIG.getPrimaryColor());
    public static final ColorScheme OPTIONS = ColorScheme.create(() -> LLibrary.CONFIG.getDarkAccentColor(), () -> 0xFF898989);
    public static final ColorScheme CLOSE = ColorScheme.create(() -> LLibrary.CONFIG.getDarkAccentColor(), () -> 0xFFE04747);
    public static final ColorScheme WINDOW = ColorScheme.create(() -> LLibrary.CONFIG.getSecondaryColor(), () -> LLibrary.CONFIG.getAccentColor());
    public static final ColorScheme TOGGLE_OFF = ColorScheme.create(() -> LLibrary.CONFIG.getSecondaryColor(), () -> LLibrary.CONFIG.getPrimarySubcolor());
    public static final ColorScheme TOGGLE_ON = ColorScheme.create(() -> LLibrary.CONFIG.getAccentColor(), () -> LLibrary.CONFIG.getDarkAccentColor());
    public static final ColorScheme DISABLED = ColorScheme.create(() -> LLibrary.CONFIG.getTertiaryColor(), null);

    private Supplier<Integer> primaryColor;
    private Supplier<Integer> secondaryColor;

    private ColorScheme(Supplier<Integer> primaryColor, Supplier<Integer> secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public static ColorScheme create(Supplier<Integer> primaryColor, Supplier<Integer> secondaryColor) {
        return new ColorScheme(primaryColor, secondaryColor);
    }

    public int getPrimaryColor() {
        return this.primaryColor.get();
    }

    public int getSecondaryColor() {
        return this.secondaryColor.get();
    }
}
