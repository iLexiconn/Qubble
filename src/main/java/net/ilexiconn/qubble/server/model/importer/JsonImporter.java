package net.ilexiconn.qubble.server.model.importer;

import com.google.gson.Gson;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.model.BlockModelContainer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonImporter implements IModelImporter<BlockModelContainer> {
    @Override
    public String getName() {
        return "Block JSON";
    }

    @Override
    public String getExtension() {
        return "json";
    }

    @Override
    public QubbleModel getModel(String fileName, BlockModelContainer model) {
        int partIndex = 0;
        QubbleModel qubbleModel = QubbleModel.create(fileName, "Unknown", 64, 32);
        for (BlockModelContainer.Element element : model.elements) {
            QubbleCube cube = QubbleCube.create(element.name == null ? "Part " + partIndex++ : element.name);
            float[] position = new float[] {element.from[0], element.from[1], element.from[2]};
            float[] offset = new float[3];
            float[] rotation = new float[3];
            int[] dimensions = new int[] {(int) (element.to[0] - position[0]), (int) (element.to[1] - position[1]), (int) (element.to[2] - position[2])};
            if (element.rotation != null) {
                BlockModelContainer.ElementRotation elementRotation = element.rotation;
                float[] origin = elementRotation.origin;
                if (origin == null) {
                    origin = new float[] {8, 8, 8};
                }
                offset[0] = origin[0] - position[0];
                offset[1] = origin[1] - position[1];
                offset[2] = origin[2] - position[2];
                position[0] -= offset[0];
                position[1] -= offset[1];
                position[2] -= offset[2];
                if (elementRotation.axis.equalsIgnoreCase("x")) {
                    rotation[0] = elementRotation.angle;
                } else if (elementRotation.axis.equalsIgnoreCase("y")) {
                    rotation[1] = elementRotation.angle;
                } else if (elementRotation.axis.equalsIgnoreCase("z")) {
                    rotation[2] = elementRotation.angle;
                }
            }
            cube.setPosition(position[0] - 8, 24 - (position[1] + dimensions[1]), position[2] - 8);
            cube.setDimensions(dimensions[0], dimensions[1], dimensions[2]);
            cube.setOffset(offset[0], offset[1], offset[2]);
            cube.setRotation(-rotation[0], -rotation[1], -rotation[2]);
            qubbleModel.getCubes().add(cube);
        }
        return qubbleModel;
    }

    @Override
    public BlockModelContainer read(File file) throws IOException {
        return new Gson().fromJson(new FileReader(file), BlockModelContainer.class);
    }
}
