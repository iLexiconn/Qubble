package net.ilexiconn.qubble.client.model.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaTexture;
import net.ilexiconn.qubble.client.model.container.BlockModelContainer;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class JsonExporter implements IModelExporter<BlockModelContainer, BlockCuboidWrapper, BlockModelWrapper> {
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
    public BlockModelContainer export(BlockModelWrapper model, String... arguments) {
        BlockModelContainer blockModel = new BlockModelContainer();

        for (BlockCuboidWrapper wrapper : model.getCuboids()) {
            QubbleVanillaCuboid cuboid = wrapper.getCuboid();
            BlockModelContainer.Element element = new BlockModelContainer.Element();
            element.name = wrapper.getName();
            element.from = new float[] { cuboid.getFromX(), cuboid.getFromY(), cuboid.getFromZ() };
            element.to = new float[] { cuboid.getToX(), cuboid.getToY(), cuboid.getToZ() };

            QubbleVanillaRotation cuboidRotation = cuboid.getRotation();
            if (cuboidRotation != null) {
                BlockModelContainer.ElementRotation rotation = new BlockModelContainer.ElementRotation();
                rotation.origin = new float[] { cuboidRotation.getOriginX(), cuboidRotation.getOriginY(), cuboidRotation.getOriginZ() };
                rotation.axis = cuboidRotation.getAxis().getName();
                rotation.angle = cuboidRotation.getAngle();
                element.rotation = rotation;
            }

            for (EnumFacing facing : EnumFacing.VALUES) {
                QubbleVanillaFace face = cuboid.getFace(facing);
                if (face != null) {
                    element.faces.put(facing.getName(), this.createFace(face));
                }
            }

            element.setShade(cuboid.hasShade());

            blockModel.elements.add(element);
        }

        Map<String, QubbleVanillaTexture> textures = model.getModel().getTextures();
        Map<QubbleVanillaTexture, ResourceLocation> textureLocations = new HashMap<>();

        for (Map.Entry<String, QubbleVanillaTexture> entry : textures.entrySet()) {
            QubbleVanillaTexture texture = entry.getValue();
            ResourceLocation resource = new ResourceLocation(texture.getTexture());
            ResourceLocation location = new ResourceLocation(arguments[0], "blocks/" + resource.getResourcePath());
            blockModel.textures.put(entry.getKey(), location.toString());
            textureLocations.put(texture, location);
        }

        for (Map.Entry<QubbleVanillaTexture, ResourceLocation> entry : textureLocations.entrySet()) {
            QubbleVanillaTexture texture = entry.getKey();
            if (texture.getBoolean("particle") && !blockModel.textures.containsKey("particle")) {
                blockModel.textures.put("particle", entry.getValue().toString());
                break;
            }
        }

        if (blockModel.textures.size() > 0 && !blockModel.textures.containsKey("particle")) {
            blockModel.textures.put("particle", this.getParticleTexture(arguments[0], model));
        }

        blockModel.ambientocclusion = model.hasAmbientOcclusion();

        return blockModel;
    }

    private String getParticleTexture(String domain, BlockModelWrapper model) {
        Map<String, Integer> weights = new HashMap<>();

        for (BlockCuboidWrapper wrapper : model.getCuboids()) {
            QubbleVanillaCuboid cuboid = wrapper.getCuboid();
            for (EnumFacing facing : EnumFacing.VALUES) {
                QubbleVanillaFace face = cuboid.getFace(facing);
                if (face != null) {
                    String texture = face.getTexture();
                    if (texture != null) {
                        int weight = (int) ((face.getMaxU() - face.getMinU()) * (face.getMaxV() - face.getMinV()));
                        int currentWeight = weights.getOrDefault(texture, 0);
                        weights.put(texture, currentWeight + weight);
                    }
                }
            }
        }

        int highestWeight = 0;
        String particleTexture = null;
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            int usages = entry.getValue();
            if (usages > highestWeight) {
                particleTexture = entry.getKey();
                highestWeight = usages;
            }
        }

        if (particleTexture != null) {
            ResourceLocation resource = new ResourceLocation(particleTexture);
            ResourceLocation location = new ResourceLocation(domain, "blocks/" + resource.getResourcePath());
            return location.toString();
        }
        return null;
    }

    private BlockModelContainer.ElementFace createFace(QubbleVanillaFace face) {
        BlockModelContainer.ElementFace elementFace = new BlockModelContainer.ElementFace();
        elementFace.texture = "#" + face.getTexture();
        elementFace.uv = new float[] { face.getMinU(), face.getMinV(), face.getMaxU(), face.getMaxV() };
        return elementFace;
    }

    @Override
    public void save(BlockModelContainer model, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(GSON.toJson(model).getBytes(Charset.forName("UTF-8")));
        out.close();
    }

    @Override
    public String[] getArgumentNames() {
        return new String[] { "Resource Domain" };
    }

    @Override
    public String[] getDefaultArguments(BlockModelWrapper currentModel) {
        return new String[] { "minecraft" };
    }

    @Override
    public String getFileName(String[] arguments, String fileName) {
        return fileName;
    }

    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.BLOCK;
    }
}
