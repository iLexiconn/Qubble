package net.ilexiconn.qubble.server.config;

import net.ilexiconn.qubble.server.color.ColorMode;

import java.awt.*;

public class QubbleConfig {
    private int accentColor = 0xFF038288;
    private ColorMode colorMode = ColorMode.DARK;

    public int getPrimaryColor() {
        return colorMode.getPrimaryColor();
    }

    public int getSecondaryColor() {
        return colorMode.getSecondaryColor();
    }

    public int getTertiaryColor() {
        return colorMode.getTertiaryColor();
    }

    public int getPrimarySubcolor() {
        return colorMode.getPrimarySubcolor();
    }

    public int getSecondarySubcolor() {
        return colorMode.getSecondarySubcolor();
    }

    public int getTextColor() {
        return colorMode.getTextColor();
    }

    public int getAccentColor() {
        return accentColor;
    }

    public int getDarkAccentColor() {
        int r = this.accentColor >> 16 & 255;
        int g = this.accentColor >> 8 & 255;
        int b = this.accentColor & 255;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.85F);
        return (0xFF << 24) | ((newColor.getRed() & 0xFF) << 16) | ((newColor.getGreen() & 0xFF) << 8) | (newColor.getBlue() & 0xFF);
    }

    public ColorMode getColorMode() {
        return colorMode;
    }

    public void setAccentColor(int accentColor) {
        this.accentColor = accentColor;
    }

    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
    }
}
