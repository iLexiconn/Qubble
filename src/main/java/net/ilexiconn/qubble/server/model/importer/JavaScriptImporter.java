package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.qubble.server.model.qubble.QubbleCube;
import net.ilexiconn.qubble.server.model.qubble.QubbleModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaScriptImporter implements IModelImporter<List<String>> {
    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public String getExtension() {
        return "js";
    }

    @Override
    public QubbleModel getModel(List<String> model) {
        List<QubbleCube> cubes = new ArrayList<>();
        int textureWidth = 64;
        int textureHeight = 32;
        int textureOffsetX = 0;
        int textureOffsetY = 0;
        String name = "Unknown";
        String author = "Unknown";
        String cube = "Unknown";
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
                cube = line.substring(line.indexOf("//") + 2, line.length());
            } else if (line.contains("setTextureOffset")) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                textureOffsetX = Integer.parseInt(values[0].trim());
                textureOffsetY = Integer.parseInt(values[1].trim());
            } else if (line.contains("addBox")) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                int[] dimension = {Integer.parseInt(values[3].trim()), Integer.parseInt(values[4].trim()), Integer.parseInt(values[5].trim())};
                float[] position = {Float.parseFloat(values[0].trim()), Float.parseFloat(values[1].trim()), Float.parseFloat(values[2].trim())};
                cubes.add(new QubbleCube(cube, new ArrayList<>(), dimension, position, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new float[]{0.0F, 0.0F, 0.0F}, new int[]{textureOffsetX, textureOffsetY}, false, 100.0F));
                textureOffsetX = 0;
                textureOffsetY = 0;
                cube = "Unknown";
            }
        }
        return new QubbleModel(name, author, 1, textureWidth, textureHeight, cubes);
    }

    @Override
    public List<String> read(File file) throws IOException {
        return FileUtils.readLines(file);
    }
}
