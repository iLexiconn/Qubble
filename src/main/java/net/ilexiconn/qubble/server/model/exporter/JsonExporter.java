package net.ilexiconn.qubble.server.model.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ilexiconn.qubble.client.model.BlockModelContainer;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

//TODO Export from Vanilla models
public class JsonExporter implements IModelExporter<BlockModelContainer, DefaultCuboidWrapper, DefaultModelWrapper> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String getName() {
        return "Vanilla JSON";
    }

    @Override
    public String getExtension() {
        return "json";
    }

    @Override
    public BlockModelContainer export(DefaultModelWrapper model, String... arguments) {
        model.unparent();
        BlockModelContainer blockModel = new BlockModelContainer();
        for (DefaultCuboidWrapper cube : model.getCuboids()) {
            BlockModelContainer.Element element = new BlockModelContainer.Element();
            int dimensionX = (int) cube.getDimensionX();
            int dimensionY = (int) cube.getDimensionY();
            int dimensionZ = (int) cube.getDimensionZ();
            element.name = cube.getName();
            float positionX = cube.getPositionX() + cube.getOffsetX() + 8;
            float positionY = 24 - (cube.getPositionY() + cube.getOffsetY() + cube.getDimensionY());
            float positionZ = cube.getPositionZ() + cube.getOffsetZ() + 8;
            Vector3f from = new Vector3f(positionX, positionY, positionZ);
            Vector3f to = new Vector3f(positionX + dimensionX, positionY + dimensionY, positionZ + dimensionZ);
            element.from = new float[] { this.clampPosition(from.getX()), this.clampPosition(from.getY()), this.clampPosition(from.getZ()) };
            element.to = new float[] { this.clampPosition(to.getX()), this.clampPosition(to.getY()), this.clampPosition(to.getZ()) };
            BlockModelContainer.ElementRotation rotation = new BlockModelContainer.ElementRotation();
            rotation.origin = new float[] { this.clampPosition(positionX - cube.getOffsetX()), this.clampPosition(positionY - cube.getOffsetY()), this.clampPosition(positionZ - cube.getOffsetZ()) };
            if (cube.getRotationZ() != 0.0F) {
                rotation.axis = "z";
                rotation.angle = -(Math.round(cube.getRotationZ() / 22.5)) * 22.5F;
            } else if (cube.getRotationY() != 0.0F) {
                rotation.axis = "y";
                rotation.angle = -(Math.round(cube.getRotationY() / 22.5)) * 22.5F;
            } else if (cube.getRotationX() != 0.0F) {
                rotation.axis = "x";
                rotation.angle = -(Math.round(cube.getRotationX() / 22.5)) * 22.5F;
            } else {
                rotation = null;
            }
            element.rotation = rotation;

            element.faces.put("down", this.createFace());
            element.faces.put("up", this.createFace());
            element.faces.put("north", this.createFace());
            element.faces.put("south", this.createFace());
            element.faces.put("west", this.createFace());
            element.faces.put("east", this.createFace());

            blockModel.elements.add(element);
        }
        return blockModel;
    }

    private BlockModelContainer.ElementFace createFace() {
        BlockModelContainer.ElementFace face = new BlockModelContainer.ElementFace();
        face.texture = "#all";
        face.uv = new float[] {0, 0, 1, 1};
        return face;
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
    public String[] getDefaultArguments(DefaultModelWrapper currentModel) {
        return new String[] {};
    }

    @Override
    public String getFileName(String[] arguments, String fileName) {
        return fileName;
    }

    private float clampPosition(float position) {
        return Math.max(-16.0F, Math.min(32.0F, position));
    }
}
