package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaModel;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaTexture;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.render.BlockRenderModel;
import net.ilexiconn.qubble.client.model.render.QubbleRenderModel;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockModelWrapper extends ModelWrapper<BlockCuboidWrapper> {
    private QubbleVanillaModel model;
    private BlockRenderModel renderModel;
    private List<BlockCuboidWrapper> cuboids = new ArrayList<>();

    public BlockModelWrapper(QubbleVanillaModel model) {
        this.model = model;
        this.rebuildModel();
        for (QubbleVanillaCuboid cuboid : model.getCuboids()) {
            this.cuboids.add(new BlockCuboidWrapper(this, cuboid));
        }
    }

    @Override
    public boolean reparent(BlockCuboidWrapper cuboid, BlockCuboidWrapper parent, boolean inPlace) {
        return false;
    }

    @Override
    public boolean supportsParenting() {
        return false;
    }

    @Override
    public void rebuildModel() {
        this.renderModel = new BlockRenderModel(this);
    }

    @Override
    public void addCuboid(BlockCuboidWrapper cuboid) {
        this.cuboids.add(cuboid);
        this.model.addCuboid(cuboid.getCuboid());
        this.rebuildModel();
    }

    @Override
    public boolean deleteCuboid(BlockCuboidWrapper cuboid) {
        String name = cuboid.getName();
        boolean removed = this.cuboids.remove(cuboid);
        this.model.removeCuboid(name);
        this.rebuildModel();
        return removed;
    }

    @Override
    public void render(boolean selection) {
        if (this.renderModel == null) {
            this.rebuildModel();
        }
        GlStateManager.pushMatrix();
        float scale = 0.0625F;
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.translate(-8.0F * scale, -24.0F * scale, -8.0F * scale);
        this.renderModel.render(selection);
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        GlStateManager.popMatrix();
    }

    @Override
    public List<BlockCuboidWrapper> getCuboids() {
        return this.cuboids;
    }

    @Override
    public String getName() {
        return this.model.getName();
    }

    @Override
    public String getFileName() {
        return this.model.getFileName();
    }

    @Override
    public String getAuthor() {
        return this.model.getAuthor();
    }

    @Override
    public ModelWrapper<BlockCuboidWrapper> copy() {
        return new BlockModelWrapper(this.model.copy());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.model.serializeNBT();
    }

    @Override
    public BlockCuboidWrapper createCuboid(String name) {
        BlockCuboidWrapper cuboid = new BlockCuboidWrapper(this, QubbleVanillaCuboid.create(name, null, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
        this.addCuboid(cuboid);
        return cuboid;
    }

    @Override
    public ModelType getType() {
        return ModelType.BLOCK;
    }

    @Override
    public QubbleRenderModel<BlockCuboidWrapper, ?> getRenderModel() {
        return this.renderModel;
    }

    @Override
    public void importTextures(Map<String, ModelTexture> textures) {
        for (Map.Entry<String, QubbleVanillaTexture> entry : this.model.getTextures().entrySet()) {
            String identifier = entry.getKey();
            ModelTexture modelTexture = textures.get(identifier);
            if (modelTexture != null) {
                modelTexture.setName(entry.getValue().getTexture());
                super.setTexture(identifier, modelTexture);
            }
        }
    }

    @Override
    public BlockCuboidWrapper createSide(BlockCuboidWrapper selectedCuboid, EnumFacing facing) {
        BlockCuboidWrapper cuboid = selectedCuboid.copy(this);
        ModelHandler.INSTANCE.applyTransformation(cuboid, ModelHandler.INSTANCE.getParentTransformation(this, selectedCuboid, true, false));
        cuboid.setOffset(selectedCuboid.getOffsetX(), selectedCuboid.getOffsetY(), selectedCuboid.getOffsetZ());

        float offsetX = cuboid.getDimensionX() * facing.getFrontOffsetX();
        float offsetY = cuboid.getDimensionY() * facing.getFrontOffsetY();
        float offsetZ = cuboid.getDimensionZ() * facing.getFrontOffsetZ();
        cuboid.setPosition(cuboid.getPositionX() + offsetX, cuboid.getPositionY() + offsetY, cuboid.getPositionZ() + offsetZ);

        this.addCuboid(cuboid);

        return cuboid;
    }

    @Override
    public void setTexture(String name, ModelTexture texture) {
        super.setTexture(name, texture);
        if (texture != null) {
            this.model.addTexture(name, ModelHandler.INSTANCE.createVanillaTexture(name, texture.getName()));
        } else {
            this.model.removeTexture(name);
            for (QubbleVanillaCuboid cuboid : this.model.getCuboids()) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    QubbleVanillaFace face = cuboid.getFace(facing);
                    String faceTexture = face.getTexture();
                    if (faceTexture != null && faceTexture.equals(name)) {
                        face.setTexture(null);
                    }
                }
            }
        }
    }

    public void renameTexture(String name, String newName) {
        if (!name.equals(newName)) {
            QubbleVanillaTexture value = this.model.getTextures().remove(name);
            if (value != null) {
                this.model.addTexture(newName, value.copy());
                super.setTexture(newName, this.getTexture(name));
                super.setTexture(name, null);
                for (QubbleVanillaCuboid cuboid : this.model.getCuboids()) {
                    for (EnumFacing facing : EnumFacing.VALUES) {
                        QubbleVanillaFace face = cuboid.getFace(facing);
                        String faceTexture = face.getTexture();
                        if (faceTexture != null && faceTexture.equals(name)) {
                            face.setTexture(newName);
                        }
                    }
                }
            }
        }
    }

    public QubbleVanillaModel getModel() {
        return this.model;
    }

    public void setAmbientOcclusion(boolean ambientOcclusion) {
        this.model.setAmbientOcclusion(ambientOcclusion);
    }

    public boolean hasAmbientOcclusion() {
        return this.model.hasAmbientOcclusion();
    }
}
