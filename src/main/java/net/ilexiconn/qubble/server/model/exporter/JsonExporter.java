package net.ilexiconn.qubble.server.model.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.model.BlockModelContainer;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class JsonExporter implements IModelExporter<BlockModelContainer> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String getName() {
        return "Block JSON";
    }

    @Override
    public String getExtension() {
        return "json";
    }

    @Override
    public BlockModelContainer export(QubbleModel model, String... arguments) {
        model.unparent();
        BlockModelContainer blockModel = new BlockModelContainer();
        for (QubbleCube cube : model.getCubes()) {
            BlockModelContainer.Element element = new BlockModelContainer.Element();
            int dimensionX = cube.getDimensionX();
            int dimensionY = cube.getDimensionY();
            int dimensionZ = cube.getDimensionZ();
            element.name = cube.getName();
            float positionX = cube.getPositionX() + cube.getOffsetX() + 8;
            float positionY = 24 - (cube.getPositionY() + cube.getOffsetY() + cube.getDimensionY());
            float positionZ = cube.getPositionZ() + cube.getOffsetZ() + 8;
            Vector3f from = new Vector3f(positionX, positionY, positionZ);
            Vector3f to = new Vector3f(positionX + dimensionX, positionY + dimensionY, positionZ + dimensionZ);
            element.from = new float[] {this.clampPosition(from.getX()), this.clampPosition(from.getY()), this.clampPosition(from.getZ())};
            element.to = new float[] {this.clampPosition(to.getX()), this.clampPosition(to.getY()), this.clampPosition(to.getZ())};
            BlockModelContainer.ElementRotation rotation = new BlockModelContainer.ElementRotation();
            rotation.origin = new float[] {this.clampPosition(positionX - cube.getOffsetX()), this.clampPosition(positionY - cube.getOffsetY()), this.clampPosition(positionZ - cube.getOffsetZ())};
            if (cube.getRotationZ() != 0.0F) {
                rotation.axis = "z";
                rotation.angle = -((int) (cube.getRotationZ() / 16)) * 22.5F;
            } else if (cube.getRotationY() != 0.0F) {
                rotation.axis = "y";
                rotation.angle = -((int) (cube.getRotationY() / 16)) * 22.5F;
            } else if (cube.getRotationX() != 0.0F) {
                rotation.axis = "x";
                rotation.angle = -((int) (cube.getRotationX() / 16)) * 22.5F;
            }
            element.rotation = rotation;
            blockModel.elements.add(element);
        }
        return blockModel;
    }

    @Override
    public void save(BlockModelContainer model, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(GSON.toJson(model).getBytes(Charset.forName("UTF-8")));
        out.close();
    }

    @Override
    public String[] getArgumentNames() {
        return new String[] {};
    }

    @Override
    public String[] getDefaultArguments(QubbleModel currentModel) {
        return new String[] {};
    }

    private float clampPosition(float position) {
        return Math.max(-16.0F, Math.min(32.0F, position));
    }
}
