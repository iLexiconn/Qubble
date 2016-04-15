package net.ilexiconn.qubble.server.config;

import net.ilexiconn.llibrary.server.config.ConfigEntry;

public class QubbleConfig {
    @ConfigEntry(name = "Accent Color")
    public String accentColor = "0xFF212121";

    @ConfigEntry(name = "Dark Mode")
    public boolean darkMode = false;

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
}
