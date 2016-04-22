package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class ModelTexture {
    private BufferedImage image;
    private ResourceLocation location;
    private DynamicTexture dynamicTexture;
    private String displayName;

    public ModelTexture(BufferedImage image, String name) {
        this.image = image;
        this.dynamicTexture = new DynamicTexture(image);
        this.displayName = name;
        this.location = ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation(Qubble.MODID + ":" + name, this.dynamicTexture);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public DynamicTexture getDynamicTexture() {
        return this.dynamicTexture;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
