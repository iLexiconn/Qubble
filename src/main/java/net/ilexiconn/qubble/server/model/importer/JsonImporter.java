package net.ilexiconn.qubble.server.model.importer;

import com.google.gson.Gson;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaModel;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.ilexiconn.qubble.client.model.BlockModelContainer;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.util.EnumFacing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonImporter implements IModelImporter<BlockModelContainer, BlockCuboidWrapper, BlockModelWrapper> {
    @Override
    public String getName() {
        return "JSON";
    }

    @Override
    public String getExtension() {
        return "json";
    }

    @Override
    public BlockModelWrapper getModel(String fileName, BlockModelContainer model) {
        int partIndex = 0;
        QubbleVanillaModel qubbleModel = QubbleVanillaModel.create(fileName, "Unknown");
        for (BlockModelContainer.Element element : model.elements) {
            String name = element.name == null ? "Part " + partIndex++ : element.name;
            QubbleVanillaCuboid cuboid = QubbleVanillaCuboid.create(name, null, element.from[0], element.from[1], element.from[2], element.to[0], element.to[1], element.to[2]);
            if (element.rotation != null) {
                BlockModelContainer.ElementRotation elementRotation = element.rotation;
                EnumFacing.Axis axis = EnumFacing.Axis.byName(elementRotation.axis);
                float[] origin = elementRotation.origin;
                if (origin == null) {
                    origin = new float[] { 8, 8, 8 };
                }
                cuboid.setRotation(QubbleVanillaRotation.create(axis, origin[0], origin[1], origin[2], elementRotation.angle));
            }
            qubbleModel.addCuboid(cuboid);
        }
        return new BlockModelWrapper(qubbleModel);
    }

    @Override
    public BlockModelContainer read(File file) throws IOException {
        return new Gson().fromJson(new FileReader(file), BlockModelContainer.class);
    }
}
