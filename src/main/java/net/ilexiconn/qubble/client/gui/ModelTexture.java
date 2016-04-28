package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class ModelTexture {
    private ResourceLocation location;
    private String displayName;

    public ModelTexture(BufferedImage image, String name) {
        this.displayName = name;
        this.location = ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation(Qubble.MODID + ":" + name, new DynamicTexture(image));
    }

    public ModelTexture(ResourceLocation texture) {
        this.location = texture;
        this.displayName = texture.toString();
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
