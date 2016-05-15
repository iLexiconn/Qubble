package net.ilexiconn.qubble.client.gui.element.color;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.color.ColorScheme;

public class ColorSchemes {
    public static final ColorScheme DEFAULT = ColorScheme.create(() -> LLibrary.CONFIG.getAccentColor(), () -> LLibrary.CONFIG.getDarkAccentColor());
    public static final ColorScheme TAB_ACTIVE = ColorScheme.create(() -> LLibrary.CONFIG.getPrimaryColor(), () -> LLibrary.CONFIG.getPrimaryColor());
    public static final ColorScheme OPTIONS = ColorScheme.create(() -> LLibrary.CONFIG.getDarkAccentColor(), () -> 0xFF898989);
    public static final ColorScheme WINDOW = ColorScheme.create(() -> LLibrary.CONFIG.getSecondaryColor(), () -> LLibrary.CONFIG.getAccentColor());
    public static final ColorScheme TOGGLE_OFF = ColorScheme.create(() -> LLibrary.CONFIG.getSecondaryColor(), () -> LLibrary.CONFIG.getPrimarySubcolor());
    public static final ColorScheme TOGGLE_ON = ColorScheme.create(() -> LLibrary.CONFIG.getAccentColor(), () -> LLibrary.CONFIG.getDarkAccentColor());
}
