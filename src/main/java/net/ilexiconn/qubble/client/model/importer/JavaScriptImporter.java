package net.ilexiconn.qubble.client.model.importer;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaScriptImporter implements IModelImporter<List<String>, DefaultCuboidWrapper, DefaultModelWrapper> {
    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public String getExtension() {
        return "js";
    }

    @Override
    public DefaultModelWrapper getModel(String fileName, List<String> model) {
        List<QubbleCuboid> cubes = new ArrayList<>();
        int textureWidth = 64;
        int textureHeight = 32;
        int textureOffsetX = 0;
        int textureOffsetY = 0;
        String name = fileName;
        String author = "Unknown";
        String cubeName = "Unknown";
        for (String line : model) {
            if (line.startsWith("//") && line.contains("by")) {
                String[] array = line.substring(2, line.length()).split(" by ");
                name = array[0];
                author = array[1];
            } else if (line.contains("setTextureSize") && textureWidth == 64 && textureHeight == 32) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                textureWidth = Integer.parseInt(values[0].trim());
                textureHeight = Integer.parseInt(values[1].trim());
            } else if (line.contains("//")) {
                cubeName = line.substring(line.indexOf("//") + 2, line.length());
            } else if (line.contains("setTextureOffset")) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                textureOffsetX = Integer.parseInt(values[0].trim());
                textureOffsetY = Integer.parseInt(values[1].trim());
            } else if (line.contains("addBox")) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                int[] dimension = {Integer.parseInt(values[3].trim()), Integer.parseInt(values[4].trim()), Integer.parseInt(values[5].trim())};
                float[] position = {Float.parseFloat(values[0].trim()), Float.parseFloat(values[1].trim()), Float.parseFloat(values[2].trim())};
                QubbleCuboid cube = QubbleCuboid.create(cubeName);
                cube.setDimensions(dimension[0], dimension[1], dimension[2]);
                cube.setPosition(position[0], position[1], position[2]);
                cube.setTexture(textureOffsetX, textureOffsetY);
                cube.setOpacity(100.0F);
                cubes.add(cube);
                textureOffsetX = 0;
                textureOffsetY = 0;
                cubeName = "Unknown";
            }
        }
        QubbleModel qubble = QubbleModel.create(name, author, textureWidth, textureHeight);
        qubble.setFileName(fileName);
        qubble.getCuboids().addAll(cubes);
        return new DefaultModelWrapper(qubble);
    }

    @Override
    public List<String> read(File file) throws IOException {
        return FileUtils.readLines(file);
    }
}
