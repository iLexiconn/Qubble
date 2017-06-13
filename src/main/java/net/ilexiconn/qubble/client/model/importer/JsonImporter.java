package net.ilexiconn.qubble.client.model.importer;

import com.google.gson.Gson;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaModel;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.model.container.BlockModelContainer;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            String name = element.name;
            if (element.name == null) {
                name = element.__comment == null ? "Part " + ++partIndex : element.__comment;
            }
            QubbleVanillaCuboid cuboid = QubbleVanillaCuboid.create(name, null, element.from[0], element.from[1], element.from[2], element.to[0], element.to[1], element.to[2]);
            if (element.rotation != null) {
                BlockModelContainer.ElementRotation elementRotation = element.rotation;
                EnumFacing.Axis axis = EnumFacing.Axis.byName(elementRotation.axis);
                float[] origin = elementRotation.origin;
                if (origin == null) {
                    origin = new float[] { 8, 8, 8 };
                }
                cuboid.setRotation(QubbleVanillaRotation.create(axis, origin[0], origin[1], origin[2], elementRotation.angle));
                cuboid.setShade(element.getShade());
            }
            for (EnumFacing facing : EnumFacing.VALUES) {
                QubbleVanillaFace face = cuboid.getFace(facing);
                face.setEnabled(false);
            }
            for (Map.Entry<String, BlockModelContainer.ElementFace> faceEntry : element.faces.entrySet()) {
                BlockModelContainer.ElementFace elementFace = faceEntry.getValue();
                EnumFacing facing = EnumFacing.byName(faceEntry.getKey());
                if (facing != null) {
                    String texture = elementFace.texture;
                    if (texture.startsWith("#")) {
                        texture = texture.substring(1);
                    }
                    float[] uv = elementFace.uv;
                    if (uv == null) {
                        float sizeX = element.to[0] - element.from[0];
                        float sizeY = element.to[1] - element.from[1];
                        float sizeZ = element.to[2] - element.from[2];
                        EnumFacing.Axis axis = facing.getAxis();
                        uv = new float[] { 0.0F, 0.0F, axis == EnumFacing.Axis.X ? sizeZ : sizeX, axis == EnumFacing.Axis.Y ? sizeZ : sizeY };
                    }
                    QubbleVanillaFace face = QubbleVanillaFace.create(facing, texture, uv[0], uv[1], uv[2], uv[3]);
                    EnumFacing cullface = EnumFacing.byName(elementFace.cullface);
                    if (cullface != null) {
                        face.setCullface(cullface);
                    }
                    cuboid.setFace(face);
                }
            }
            qubbleModel.addCuboid(cuboid);
            qubbleModel.setAmbientOcclusion(model.ambientocclusion);
        }
        Map<String, ModelTexture> textures = new HashMap<>();
        for (Map.Entry<String, String> textureEntry : model.textures.entrySet()) {
            String identifier = textureEntry.getKey();
            String[] path = textureEntry.getValue().split("/");
            ResourceLocation location = new ResourceLocation(path[path.length - 1]);
            ResourceLocation value = new ResourceLocation(textureEntry.getValue());
            ResourceLocation resource = new ResourceLocation(location.getResourceDomain(), "textures/" + value.getResourcePath() + ".png");
            ModelTexture modelTexture = new ModelTexture(resource);
            modelTexture.setName(location.toString());
            textures.put(identifier, modelTexture);
            qubbleModel.addTexture(identifier, ModelHandler.INSTANCE.createVanillaTexture(identifier, location.toString()));
        }
        BlockModelWrapper wrapper = new BlockModelWrapper(qubbleModel);
        wrapper.importTextures(textures);
        return wrapper;
    }

    @Override
    public BlockModelContainer read(File file) throws IOException {
        return new Gson().fromJson(new FileReader(file), BlockModelContainer.class);
    }
}
