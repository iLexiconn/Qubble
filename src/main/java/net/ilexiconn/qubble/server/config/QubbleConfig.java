package net.ilexiconn.qubble.server.config;

import net.ilexiconn.llibrary.server.config.ConfigEntry;

import java.awt.*;

public class QubbleConfig {
    @ConfigEntry(name = "Accent Color", validValues = {"0xFEBA01", "0xFF8B00", "0xF7620E", "0xCA500F", "0xDA3B01", "0xEF6950", "0xD03438", "0xFF4244",
            "0xE64856", "0xE81123", "0xEA005F", "0xC40052", "0xE3008B", "0xBE0177", "0xC339B3", "0x9B008A",
            "0x0177D7", "0x0063B1", "0x928FD6", "0x6B69D6", "0x8764B8", "0x744DA8", "0xB046C2", "0x871797",
            "0xE64856", "0x2D7C9A", "0x01B7C4", "0x038288", "0x00B294", "0x018675", "0x00CE70", "0x10883E",
            "0x7A7474", "0x5E5A57", "0x677689", "0x505C6A", "0x577C74", "0x496860", "0x4A8205", "0x107C0F",
            "0x767676", "0x4B4A48", "0x6A797E", "0x4C535B", "0x647C64", "0x535D54", "0x837544", "0x7E735F"})
    public String accentColor = "0x038288";

    @ConfigEntry(name = "Mode", validValues = {"dark", "light"})
    public String mode = "dark";

    public int getPrimaryColor() {
        return this.mode.equals("dark") ? 0xFF212121 : 0xFFCDCDCD;
    }

    public int getSecondaryColor() {
        return this.mode.equals("dark") ? 0xFF363636 : 0xFFACACAC;
    }

    public int getTertiaryColor() {
        return this.mode.equals("dark") ? 0xFF464646 : 0xFFECECEC;
    }

    public int getPrimarySubcolor() {
        return this.mode.equals("dark") ? 0xFF212121 : 0xFFCDCDCD;
    }

    public int getSecondarySubcolor() {
        return this.mode.equals("dark") ? 0xFF1F1F1F : 0xFFC2C2C2;
    }

    public int getTextColor() {
        return this.mode.equals("dark") ? 0xFFFFFFFF : 0xFF000000;
    }

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
