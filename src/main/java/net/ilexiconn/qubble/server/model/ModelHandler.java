package net.ilexiconn.qubble.server.model;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.ModelExporters;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.ModelImporters;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public enum ModelHandler {
    INSTANCE;

    private static final Random IDENTIFIER_RANDOM = new Random();

    private List<IModelImporter<?, ?, ?>> modelImporterList = new ArrayList<>();
    private List<IModelExporter<?, ?, ?>> modelExporterList = new ArrayList<>();

    public ModelWrapper loadModel(String name, IModelImporter importer) throws IOException {
        ModelWrapper model = null;
        if (importer == null) {
            File file = new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, name + ".qbl");
            try (ZipFile zipFile = new ZipFile(file)) {
                Map<String, ModelTexture> textures = new HashMap<>();
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    String[] directory = entryName.split("/");
                    String simpleName = directory.length > 1 ? directory[directory.length - 1] : entryName;
                    if (entryName.equals("model.nbt")) {
                        NBTTagCompound compound = CompressedStreamTools.read(new DataInputStream(zipFile.getInputStream(entry)));
                        ModelType type;
                        int typeValue = compound.getByte("type");
                        if (ModelType.TYPES.containsKey(typeValue)) {
                            type = ModelType.TYPES.get(typeValue);
                        } else {
                            type = ModelType.DEFAULT;
                        }
                        model = type.deserialize(compound);
                    } else if (entryName.endsWith(".png")) {
                        InputStream in = zipFile.getInputStream(entry);
                        String hash = DigestUtils.md5Hex(in);
                        in.close();
                        in = zipFile.getInputStream(entry);
                        File match = null;
                        if (ClientProxy.QUBBLE_TEXTURE_DIRECTORY.exists()) {
                            for (File texture : ClientProxy.QUBBLE_TEXTURE_DIRECTORY.listFiles()) {
                                FileInputStream data = new FileInputStream(texture);
                                String fileHash = DigestUtils.md5Hex(data);
                                data.close();
                                if (fileHash.equals(hash)) {
                                    match = texture;
                                    break;
                                }
                            }
                        }
                        String textureName = simpleName.substring(0, simpleName.length() - 4);
                        if (match != null) {
                            textures.put(textureName, new ModelTexture(match, textureName));
                        } else {
                            BufferedImage image = ImageIO.read(in);
                            textures.put(textureName, new ModelTexture(image, textureName));
                        }
                        in.close();
                    }
                }
                if (model != null) {
                    model.importTextures(textures);
                }
                return model;
            } catch (ZipException zipException) {
                return this.loadLegacy(file);
            }
        } else {
            return importer.getModel(name, importer.read(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, name + "." + importer.getExtension())));
        }
    }

    public ModelWrapper loadLegacy(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            NBTTagCompound compound = CompressedStreamTools.readCompressed(in);
            ModelType type;
            int typeValue = compound.getByte("type");
            if (ModelType.TYPES.containsKey(typeValue)) {
                type = ModelType.TYPES.get(typeValue);
            } else {
                type = ModelType.DEFAULT;
            }
            return type.deserialize(compound);
        } catch (IOException e) {
            throw e;
        }
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> void saveModel(MDL model, String fileName) throws IOException {
        NBTTagCompound compound = model.copyModel().serializeNBT();
        compound.setByte("type", (byte) model.getType().id());
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, fileName + ".qbl")));
        ZipEntry modelEntry = new ZipEntry("model.nbt");
        out.putNextEntry(modelEntry);
        CompressedStreamTools.write(compound, new DataOutputStream(out));
        out.closeEntry();
        Map<String, ModelTexture> textures = model.getTextures();
        for (Map.Entry<String, ModelTexture> entry : textures.entrySet()) {
            ZipEntry zipEntry = new ZipEntry("textures/" + entry.getKey() + ".png");
            out.putNextEntry(zipEntry);
            entry.getValue().write(out);
            out.closeEntry();
        }
        out.close();
    }

    public void registerModelImporter(IModelImporter<?, ?, ?> modelImporter) {
        this.modelImporterList.add(modelImporter);
    }

    public void registerModelExporter(IModelExporter<?, ?, ?> modelExporter) {
        this.modelExporterList.add(modelExporter);
    }

    public List<IModelImporter<?, ?, ?>> getImporters() {
        List<IModelImporter<?, ?, ?>> list = new ArrayList<>();
        for (ModelImporters importer : ModelImporters.VALUES) {
            list.add(importer.getModelImporter());
        }
        list.addAll(this.modelImporterList);
        return list;
    }

    public List<IModelExporter<?, ?, ?>> getExporters() {
        List<IModelExporter<?, ?, ?>> list = new ArrayList<>();
        for (ModelExporters exporter : ModelExporters.VALUES) {
            list.add(exporter.getModelExporter());
        }
        list.addAll(this.modelExporterList);
        return list;
    }

    public IModelImporter<?, ?, ?> getImporter(String name) {
        IModelImporter<?, ?, ?> modelImporter = ModelImporters.getBuiltinImporter(name);
        if (modelImporter != null) {
            return modelImporter;
        } else {
            for (IModelImporter<?, ?, ?> importer : this.modelImporterList) {
                if (importer.getName().equals(name)) {
                    return importer;
                }
            }
        }
        return null;
    }

    public IModelExporter<?, ?, ?> getExporter(String name) {
        IModelExporter<?, ?, ?> modelExporter = ModelExporters.getBuiltinExporter(name);
        if (modelExporter != null) {
            return modelExporter;
        } else {
            for (IModelExporter<?, ?, ?> exporter : this.modelExporterList) {
                if (exporter.getName().equals(name)) {
                    return exporter;
                }
            }
        }
        return null;
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> String getCopyName(MDL model, String name) {
        while (name.matches("^.+?\\d$")) {
            name = name.substring(0, name.length() - 1).trim();
        }
        int index = 2;
        String newName = name;
        while (this.hasDuplicateName(model, newName)) {
            newName = name + " " + index;
            index++;
        }
        return newName;
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> boolean hasDuplicateName(MDL model, String name) {
        for (CBE cuboid : model.getCuboids()) {
            if (this.hasDuplicateName(cuboid, name)) {
                return true;
            }
        }
        return false;
    }

    protected <CBE extends CuboidWrapper<CBE>> boolean hasDuplicateName(CBE cuboid, String name) {
        if (cuboid.getName().trim().equals(name.trim())) {
            return true;
        }
        for (CBE child : cuboid.getChildren()) {
            if (this.hasDuplicateName(child, name)) {
                return true;
            }
        }
        return false;
    }

    public <CBE extends CuboidWrapper<CBE>> boolean removeChildCuboid(CBE parent, CBE cuboid) {
        boolean isChild = false;
        for (CBE currentCuboid : parent.getChildren()) {
            if (currentCuboid.equals(cuboid)) {
                isChild = true;
                break;
            }
            if (this.removeChildCuboid(currentCuboid, cuboid)) {
                return true;
            }
        }
        if (isChild) {
            parent.removeChild(cuboid);
            return true;
        }
        return false;
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> boolean removeCuboid(MDL model, CBE cuboid) {
        for (CBE currentCuboid : model.getCuboids()) {
            if (ModelHandler.INSTANCE.removeChildCuboid(currentCuboid, cuboid)) {
                return true;
            }
        }
        return model.deleteCuboid(cuboid);
    }

    public QubbleCuboid copy(DefaultModelWrapper model, DefaultCuboidWrapper cuboid) {
        QubbleCuboid copy = QubbleCuboid.create(this.getCopyName(model, cuboid.getName()));
        copy.getChildren().addAll(cuboid.getChildren().stream().map((child) -> ModelHandler.INSTANCE.copy(model, child)).collect(Collectors.toList()));
        copy.setDimensions((int) cuboid.getDimensionX(), (int) cuboid.getDimensionY(), (int) cuboid.getDimensionZ());
        copy.setPosition(cuboid.getPositionX(), cuboid.getPositionY(), cuboid.getPositionZ());
        copy.setOffset(cuboid.getOffsetX(), cuboid.getOffsetY(), cuboid.getOffsetZ());
        copy.setRotation(cuboid.getRotationX(), cuboid.getRotationY(), cuboid.getRotationZ());
        copy.setScale(cuboid.getScaleX(), cuboid.getScaleY(), cuboid.getScaleZ());
        copy.setTexture(cuboid.getTextureX(), cuboid.getTextureY());
        copy.setTextureMirrored(cuboid.isTextureMirrored());
        copy.setOpacity(cuboid.getOpacity());
        if (copy.getIdentifier() != null) {
            copy.setIdentifier(this.generateIdentifier(model.getModel()));
        }
        return copy;
    }

    public String generateIdentifier(QubbleModel model) {
        String identifier = null;
        while (identifier == null || this.hasIdentifier(model, identifier)) {
            identifier = RandomStringUtils.random(20, 32, 127, false, false, null, IDENTIFIER_RANDOM);
        }
        return identifier;
    }

    public QubbleVanillaCuboid copy(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
        QubbleVanillaCuboid copy = cuboid.getCuboid().copy();
        copy.setName(this.getCopyName(model, copy.getName()));
        return copy;
    }

    private boolean hasIdentifier(QubbleModel model, String identifier) {
        for (QubbleCuboid cuboid : model.getCuboids()) {
            if (this.hasIdentifier(cuboid, identifier)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasIdentifier(QubbleCuboid cuboid, String identifier) {
        if (cuboid.getIdentifier() != null && cuboid.getIdentifier().equals(identifier)) {
            return true;
        }
        for (QubbleCuboid child : cuboid.getChildren()) {
            if (this.hasIdentifier(child, identifier)) {
                return true;
            }
        }
        return false;
    }
}
