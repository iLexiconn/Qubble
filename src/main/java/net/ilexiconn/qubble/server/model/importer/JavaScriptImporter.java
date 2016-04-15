package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.qubble.server.model.QubbleCube;
import net.ilexiconn.qubble.server.model.QubbleModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaScriptImporter implements IModelImporter<List<String>> {
    @Override
    public QubbleModel getModel(List<String> model) {
        List<QubbleCube> cubes = new ArrayList<>();
        int textureWidth = 64;
        int textureHeight = 32;
        int textureOffsetX = 0;
        int textureOffsetY = 0;
        String name = "Unknown";
        for (String line : model) {
            if (line.contains("setTextureSize") && textureWidth == 64 && textureHeight == 32) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                textureWidth = Integer.parseInt(values[0].trim());
                textureHeight = Integer.parseInt(values[1].trim());
            } else if (line.contains("//")) {
                name = line.substring(line.indexOf("//") + 1, line.length());
            } else if (line.contains("setTextureOffset")) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                textureOffsetX = Integer.parseInt(values[0].trim());
                textureOffsetY = Integer.parseInt(values[1].trim());
            } else if (line.contains("addBox")) {
                String[] values = line.substring(line.indexOf("(") + 1, line.length() - 2).split(",");
                int[] dimension = {Integer.parseInt(values[3].trim()),Integer.parseInt(values[4].trim()), Integer.parseInt(values[5].trim())};
                float[] position = {Float.parseFloat(values[0].trim()), Float.parseFloat(values[1].trim()), Float.parseFloat(values[2].trim())};
                cubes.add(new QubbleCube(name, new ArrayList<>(), dimension, position, new float[] {0.0F, 0.0F, 0.0F}, new float[] {0.0F, 0.0F, 0.0F}, new float[] {0.0F, 0.0F, 0.0F}, new int[] {textureOffsetX, textureOffsetY}, false, 100.0F));
                textureOffsetX = 0;
                textureOffsetY = 0;
                name = "Unknown";
            }
        }
        String author = "Unknown";
        String comment = model.get(0);
        if (comment.startsWith("//") && comment.contains("by")) {
            String[] array = comment.substring(2, comment.length()).split(" by ");
            name = array[0];
            author = array[1];
        }
        return new QubbleModel(name, author, 1, textureWidth, textureHeight, cubes);
    }

    @Override
    public List<String> read(File file) throws IOException {
        return FileUtils.readLines(file);
    }
}
