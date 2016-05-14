package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ModelTexture {
    private ResourceLocation location;
    private String name;
    private String textureMD5;
    private File file;

    public ModelTexture(File file, String name) {
        this.file = file;
        this.name = name;
        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException {
        if (this.file != null) {
            FileInputStream in = new FileInputStream(this.file);
            this.textureMD5 = DigestUtils.md5Hex(in);
            in.close();
            BufferedImage image = ImageIO.read(this.file);
            this.location = ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation(Qubble.MODID + ":" + this.name, new DynamicTexture(image));
        }
    }

    public ModelTexture(ResourceLocation texture) {
        this.location = texture;
        this.name = texture.toString();
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public void update() {
        if (this.file != null) {
            try {
                FileInputStream in = new FileInputStream(this.file);
                if (!DigestUtils.md5Hex(in).equals(this.textureMD5)) {
                    this.load();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
