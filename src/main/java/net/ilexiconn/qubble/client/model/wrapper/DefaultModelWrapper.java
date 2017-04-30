package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.render.DefaultRenderModel;
import net.ilexiconn.qubble.client.model.render.QubbleRenderModel;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.vecmath.Matrix4d;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultModelWrapper extends ModelWrapper<DefaultCuboidWrapper> {
    private QubbleModel model;
    private List<DefaultCuboidWrapper> cuboids = new ArrayList<>();
    private DefaultRenderModel renderModel;

    public DefaultModelWrapper(QubbleModel model) {
        this.model = model;
        for (QubbleCuboid cuboid : model.getCuboids()) {
            this.cuboids.add(new DefaultCuboidWrapper(this, cuboid));
        }
        this.rebuildModel();
    }

    @Override
    public boolean reparent(DefaultCuboidWrapper cuboid, DefaultCuboidWrapper parent, boolean inPlace) {
        DefaultCuboidWrapper prevParent = ModelHandler.INSTANCE.getParent(this, cuboid);
        if (!ModelHandler.INSTANCE.hasChild(cuboid, parent)) {
            if (parent != cuboid) {
                if (inPlace) {
                    ModelHandler.INSTANCE.maintainParentTransformation(this, cuboid);
                    if (parent != null) {
                        ModelHandler.INSTANCE.inheritParentTransformation(this, cuboid, parent);
                    }
                }
                this.deleteCuboid(cuboid);
                if (parent != null && parent != prevParent) {
                    if (!parent.hasChild(cuboid)) {
                        parent.addChild(cuboid);
                    }
                } else if (parent == null) {
                    this.addCuboid(cuboid);
                }
                if (prevParent != null && parent != prevParent) {
                    prevParent.removeChild(cuboid);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supportsParenting() {
        return true;
    }

    @Override
    public void rebuildModel() {
        this.renderModel = new DefaultRenderModel(this);
    }

    @Override
    public void addCuboid(DefaultCuboidWrapper cuboid) {
        this.cuboids.add(cuboid);
        this.model.getCuboids().add(cuboid.getCuboid());
        this.rebuildModel();
    }

    @Override
    public boolean deleteCuboid(DefaultCuboidWrapper cuboid) {
        boolean removed = this.cuboids.remove(cuboid);
        this.model.getCuboids().remove(cuboid.getCuboid());
        this.rebuildModel();
        return removed;
    }

    @Override
    public void render(boolean selection) {
        if (this.renderModel == null) {
            this.rebuildModel();
        }
        if (this.getBaseTexture() != null && !selection) {
            GlStateManager.enableTexture2D();
            ClientProxy.MINECRAFT.getTextureManager().bindTexture(this.getBaseTexture().getLocation());
        }
        this.renderModel.render(selection);
        if (this.getOverlayTexture() != null && !selection) {
            GlStateManager.enableTexture2D();
            ClientProxy.MINECRAFT.getTextureManager().bindTexture(this.getOverlayTexture().getLocation());
            this.renderModel.render(false);
        }
    }

    @Override
    public List<DefaultCuboidWrapper> getCuboids() {
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
    public ModelWrapper<DefaultCuboidWrapper> copy() {
        return new DefaultModelWrapper(this.model.copy());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.model.serializeNBT();
    }

    @Override
    public DefaultCuboidWrapper createCuboid(String name) {
        QubbleCuboid cuboid = QubbleCuboid.create(name);
        cuboid.setDimensions(1, 1, 1);
        cuboid.setScale(1.0F, 1.0F, 1.0F);
        DefaultCuboidWrapper wrapper = new DefaultCuboidWrapper(this, cuboid);
        this.addCuboid(wrapper);
        return wrapper;
    }

    @Override
    public ModelType getType() {
        return ModelType.DEFAULT;
    }

    @Override
    public QubbleRenderModel<DefaultCuboidWrapper, ?> getRenderModel() {
        return this.renderModel;
    }

    @Override
    public void importTextures(Map<String, ModelTexture> textures) {
        for (Map.Entry<String, String> entry : this.model.getTextures().entrySet()) {
            ModelTexture texture = textures.get(entry.getKey());
            if (texture != null) {
                texture.setName(entry.getValue());
                super.setTexture(entry.getKey(), texture);
            }
        }
    }

    @Override
    public DefaultCuboidWrapper createSide(DefaultCuboidWrapper selectedCuboid, EnumFacing facing) {
        DefaultCuboidWrapper cuboid = this.createCuboid(ModelHandler.INSTANCE.getCopyName(this, selectedCuboid.getName()));
        cuboid.setDimensions(selectedCuboid.getDimensionX(), selectedCuboid.getDimensionY(), selectedCuboid.getDimensionZ());
        cuboid.setTexture(selectedCuboid.getTextureX(), selectedCuboid.getTextureY());
        cuboid.setTextureMirrored(selectedCuboid.isTextureMirrored());

        float offsetX = cuboid.getDimensionX() * facing.getFrontOffsetX();
        float offsetY = cuboid.getDimensionY() * facing.getFrontOffsetY();
        float offsetZ = cuboid.getDimensionZ() * facing.getFrontOffsetZ();
        cuboid.setPosition(cuboid.getPositionX() - offsetX, cuboid.getPositionY() - offsetY, cuboid.getPositionZ() - offsetZ);

        Matrix4d matrix = ModelHandler.INSTANCE.getParentTransformationMatrix(this, selectedCuboid, true, false);
        matrix.mul(ModelHandler.INSTANCE.getParentTransformationMatrix(this, cuboid, false, false));
        float[][] parentTransformation = ModelHandler.INSTANCE.getParentTransformation(matrix);
        ModelHandler.INSTANCE.applyTransformation(cuboid, parentTransformation);

        cuboid.setOffset(selectedCuboid.getOffsetX(), selectedCuboid.getOffsetY(), selectedCuboid.getOffsetZ());

        return cuboid;
    }

    @Override
    public void setTexture(String name, ModelTexture texture) {
        super.setTexture(name, texture);
        this.model.setTexture(name, texture != null ? texture.getName() : null);
    }

    public int getTextureWidth() {
        return this.model.getTextureWidth();
    }

    public int getTextureHeight() {
        return this.model.getTextureHeight();
    }

    public void setTextureWidth(int width) {
        this.model.setTextureWidth(width);
    }

    public void setTextureHeight(int height) {
        this.model.setTextureHeight(height);
    }

    public QubbleModel getModel() {
        return this.model;
    }

    public DefaultModelWrapper unparent() {
        return new DefaultModelWrapper(this.model.unparent());
    }
}
