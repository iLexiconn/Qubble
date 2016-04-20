package net.ilexiconn.qubble.server.color;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.server.util.IGetter;

public class ColorScheme {
    public static final ColorScheme DEFAULT = ColorScheme.create(() -> Qubble.CONFIG.getAccentColor(), () -> Qubble.CONFIG.getDarkAccentColor());
    public static final ColorScheme TAB_ACTIVE = ColorScheme.create(() -> Qubble.CONFIG.getPrimaryColor(), () -> Qubble.CONFIG.getPrimaryColor());
    public static final ColorScheme OPTIONS = ColorScheme.create(() -> Qubble.CONFIG.getDarkAccentColor(), () -> 0xFF898989);
    public static final ColorScheme CLOSE = ColorScheme.create(() -> Qubble.CONFIG.getDarkAccentColor(), () -> 0xFFE04747);
    public static final ColorScheme WINDOW = ColorScheme.create(() -> Qubble.CONFIG.getSecondaryColor(), () -> Qubble.CONFIG.getSecondarySubcolor());

    private IGetter<Integer> primaryColor;
    private IGetter<Integer> secondaryColor;

    private ColorScheme(IGetter<Integer> primaryColor, IGetter<Integer> secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public static ColorScheme create(IGetter<Integer> primaryColor, IGetter<Integer> secondaryColor) {
        return new ColorScheme(primaryColor, secondaryColor);
    }

    public int getPrimaryColor() {
        return this.primaryColor.get();
    }

    public int getSecondaryColor() {
        return this.secondaryColor.get();
    }
}
