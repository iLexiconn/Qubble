package net.ilexiconn.qubble.server.config;

import net.ilexiconn.llibrary.server.nbt.NBTHandler;
import net.ilexiconn.llibrary.server.nbt.NBTMutatorProperty;
import net.ilexiconn.llibrary.server.nbt.NBTProperty;
import net.ilexiconn.qubble.server.color.ColorMode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.awt.*;
import java.util.Locale;

public class QubbleConfig implements INBTSerializable<NBTTagCompound> {
    @NBTProperty
    private int accentColor = 0xFF038288;
    @NBTMutatorProperty(type = String.class)
    private ColorMode colorMode = ColorMode.LIGHT;

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

    public String getColorMode() {
        return colorMode.getName();
    }

    public void setAccentColor(int accentColor) {
        this.accentColor = accentColor;
    }

    public void setColorMode(String colorMode) {
        try {
            this.colorMode = (ColorMode) ColorMode.class.getField(colorMode.toUpperCase(Locale.ENGLISH)).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            this.colorMode = ColorMode.LIGHT;
            e.printStackTrace();
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTHandler.INSTANCE.saveNBTData(this, compound);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        NBTHandler.INSTANCE.loadNBTData(this, compound);
    }
}
