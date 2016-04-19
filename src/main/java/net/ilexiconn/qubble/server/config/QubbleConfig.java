package net.ilexiconn.qubble.server.config;

import net.ilexiconn.qubble.server.mode.ColorModes;

import java.awt.*;

public class QubbleConfig {
    public String accentColor = "0x038288";
    public ColorModes colorMode = ColorModes.DARK;

    public int getAccentColor() {
        if (this.accentColor.startsWith("0x")) {
            String hex = this.accentColor.split("0x")[1];
            while (hex.length() < 8) {
                hex = "F" + hex;
            }
            return (int) Long.parseLong(hex, 16);
        } else {
            return Integer.parseInt(this.accentColor);
        }
    }

    public int getDarkerColor(int color) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.85F);
        return (0xFF << 24) | ((newColor.getRed() & 0xFF) << 16) | ((newColor.getGreen() & 0xFF) << 8) | (newColor.getBlue() & 0xFF);
    }
}
