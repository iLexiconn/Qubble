package net.ilexiconn.qubble.server.model.exporter;

import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureMapExporter implements IModelExporter<BufferedImage, DefaultCuboidWrapper, DefaultModelWrapper> {
    @Override
    public String getName() {
        return "Texture Map";
    }

    @Override
    public String getExtension() {
        return "png";
    }

    @Override
    public BufferedImage export(DefaultModelWrapper model, String... arguments) {
        model.unparent();
        BufferedImage texture = new BufferedImage(model.getTextureWidth(), model.getTextureHeight(), BufferedImage.TYPE_INT_ARGB);
        for (DefaultCuboidWrapper cube : model.getCuboids()) {
            int textureX = (int) cube.getTextureX();
            int textureY = (int) cube.getTextureY();
            int dimensionX = (int) cube.getDimensionX();
            int dimensionY = (int) cube.getDimensionY();
            int dimensionZ = (int) cube.getDimensionZ();
            this.fill(texture, textureX + dimensionZ, textureY, dimensionX, dimensionZ, 0xFF00FF00);
            this.fill(texture, textureX + dimensionX + dimensionZ, textureY, dimensionX, dimensionZ, 0xFF00AA00);
            this.fill(texture, textureX, textureY + dimensionZ, dimensionZ, dimensionY, 0xFFFF0000);
            this.fill(texture, textureX + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, 0xFF0000FF);
            this.fill(texture, textureX + dimensionX + dimensionZ, textureY + dimensionZ, dimensionZ, dimensionY, 0xFFAA0000);
            this.fill(texture, textureX + dimensionX + dimensionZ + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, 0xFF0000AA);
        }
        return texture;
    }

    @Override
    public void save(BufferedImage model, File file) throws IOException {
        ImageIO.write(model, this.getExtension(), file);
    }

    @Override
    public String[] getArgumentNames() {
        return new String[]{};
    }

    @Override
    public String[] getDefaultArguments(DefaultModelWrapper currentModel) {
        return new String[]{};
    }

    @Override
    public String getFileName(String[] arguments, String fileName) {
        return fileName;
    }

    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.DEFAULT;
    }

    private void fill(BufferedImage image, int x, int y, int width, int height, int color) {
        for (int textureX = x; textureX < x + width; textureX++) {
            for (int textureY = y; textureY < y + height; textureY++) {
                if (textureX >= 0 && textureY >= 0 && textureX < image.getWidth() && textureY < image.getHeight()) {
                    image.setRGB(textureX, textureY, color);
                }
            }
        }
    }
}