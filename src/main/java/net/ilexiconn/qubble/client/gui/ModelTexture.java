package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ModelTexture {
    public static final String BASE = "base";
    public static final String OVERLAY = "overlay";

    private ResourceLocation location;
    private String name;
    private String textureMD5;
    private File file;
    private BufferedImage image;

    public ModelTexture(File file, String name) {
        this.file = file;
        this.name = name;
        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ModelTexture(ResourceLocation texture) {
        this.location = texture;
        this.name = texture.toString();
    }

    public ModelTexture(BufferedImage image, String name) {
        this.name = name;
        this.location = ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation(Qubble.MODID + ":" + this.name, new DynamicTexture(image));
        this.image = image;
    }

    private void load() throws IOException {
        if (this.file != null) {
            FileInputStream in = new FileInputStream(this.file);
            this.textureMD5 = DigestUtils.md5Hex(in);
            in.close();
            BufferedImage image = ImageIO.read(this.file);
            if (this.location != null) {
                ClientProxy.MINECRAFT.getTextureManager().deleteTexture(this.location);
            }
            this.location = ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation(Qubble.MODID + ":" + this.name, new DynamicTexture(image));
        }
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
                this.file = null;
                e.printStackTrace();
            }
        }
    }

    public void write(OutputStream out) throws IOException {
        if (this.file != null) {
            IOUtils.copy(new FileInputStream(this.file), out);
        } else if (this.image != null) {
            ImageIO.write(this.image, "png", out);
        } else {
            IOUtils.copy(ClientProxy.MINECRAFT.getResourceManager().getResource(this.location).getInputStream(), out);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelTexture copy() {
        if (this.file != null) {
            return new ModelTexture(this.file, this.name);
        } else if (this.image != null) {
            return new ModelTexture(this.image, this.name);
        } else {
            ModelTexture modelTexture = new ModelTexture(this.location);
            modelTexture.setName(this.name);
            return modelTexture;
        }
    }
}
