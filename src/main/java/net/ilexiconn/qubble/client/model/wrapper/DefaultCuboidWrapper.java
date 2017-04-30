package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class DefaultCuboidWrapper implements CuboidWrapper<DefaultCuboidWrapper> {
    private DefaultModelWrapper modelWrapper;
    private QubbleCuboid cuboid;
    private List<DefaultCuboidWrapper> children = new ArrayList<>();

    public DefaultCuboidWrapper(DefaultModelWrapper model, QubbleCuboid cuboid) {
        this.modelWrapper = model;
        this.cuboid = cuboid;
        for (QubbleCuboid child : cuboid.getChildren()) {
            this.children.add(new DefaultCuboidWrapper(model, child));
        }
    }

    @Override
    public void setName(String name) {
        this.cuboid.setName(name);
    }

    @Override
    public String getName() {
        return this.cuboid.getName();
    }

    public QubbleCuboid getCuboid() {
        return this.cuboid;
    }

    @Override
    public List<DefaultCuboidWrapper> getChildren() {
        return this.children;
    }

    @Override
    public float getRotationX() {
        return this.cuboid.getRotationX();
    }

    @Override
    public float getRotationY() {
        return this.cuboid.getRotationY();
    }

    @Override
    public float getRotationZ() {
        return this.cuboid.getRotationZ();
    }

    @Override
    public float getPositionX() {
        return this.cuboid.getPositionX();
    }

    @Override
    public float getPositionY() {
        return this.cuboid.getPositionY();
    }

    @Override
    public float getPositionZ() {
        return this.cuboid.getPositionZ();
    }

    @Override
    public float getOffsetX() {
        return this.cuboid.getOffsetX();
    }

    @Override
    public float getOffsetY() {
        return this.cuboid.getOffsetY();
    }

    @Override
    public float getOffsetZ() {
        return this.cuboid.getOffsetZ();
    }

    @Override
    public float getDimensionX() {
        return this.cuboid.getDimensionX();
    }

    @Override
    public float getDimensionY() {
        return this.cuboid.getDimensionY();
    }

    @Override
    public float getDimensionZ() {
        return this.cuboid.getDimensionZ();
    }

    @Override
    public float getMinU(EnumFacing facing) {
        return 0.0F;
    }

    @Override
    public float getMinV(EnumFacing facing) {
        return 0.0F;
    }

    @Override
    public float getMaxU(EnumFacing facing) {
        return 0.0F;
    }

    @Override
    public float getMaxV(EnumFacing facing) {
        return 0.0F;
    }

    @Override
    public float getScaleX() {
        return this.cuboid.getScaleX();
    }

    @Override
    public float getScaleY() {
        return this.cuboid.getScaleY();
    }

    @Override
    public float getScaleZ() {
        return this.cuboid.getScaleZ();
    }

    @Override
    public int getTextureX() {
        return this.cuboid.getTextureX();
    }

    @Override
    public int getTextureY() {
        return this.cuboid.getTextureY();
    }

    @Override
    public void setRotation(float x, float y, float z) {
        this.cuboid.setRotation(x, y, z);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.cuboid.setPosition(x, y, z);
    }

    @Override
    public void setScale(float x, float y, float z) {
        this.cuboid.setScale(x, y, z);
    }

    @Override
    public void setOffset(float x, float y, float z) {
        this.cuboid.setOffset(x, y, z);
    }

    @Override
    public void setDimensions(float x, float y, float z) {
        this.cuboid.setDimensions((int) x, (int) y, (int) z);
    }

    @Override
    public void setTexture(float x, float y) {
        this.cuboid.setTexture((int) x, (int) y);
    }

    @Override
    public void setMinU(EnumFacing facing, float minU) {
    }

    @Override
    public void setMinV(EnumFacing facing, float minV) {
    }

    @Override
    public void setMaxU(EnumFacing facing, float maxU) {
    }

    @Override
    public void setMaxV(EnumFacing facing, float maxV) {
    }

    @Override
    public void addChild(DefaultCuboidWrapper cuboid) {
        this.children.add(cuboid);
        this.cuboid.getChildren().add(cuboid.getCuboid());
    }

    @Override
    public void removeChild(DefaultCuboidWrapper cuboid) {
        this.children.remove(cuboid);
        this.cuboid.getChildren().remove(cuboid.getCuboid());
    }

    @Override
    public DefaultCuboidWrapper copy(ModelWrapper<DefaultCuboidWrapper> model) {
        DefaultModelWrapper defaultModel = (DefaultModelWrapper) model;
        return new DefaultCuboidWrapper(defaultModel, ModelHandler.INSTANCE.copy(defaultModel, this));
    }

    @Override
    public DefaultCuboidWrapper copyRaw() {
        return new DefaultCuboidWrapper(this.modelWrapper, this.cuboid.copy());
    }

    @Override
    public boolean hasChild(DefaultCuboidWrapper cuboid) {
        return this.children.contains(cuboid);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.DEFAULT;
    }

    @Override
    public void setAutoUV() {
    }

    @Override
    public void setTextureMirrored(boolean textureMirrored) {
        this.cuboid.setTextureMirrored(textureMirrored);
    }

    @Override
    public boolean isTextureMirrored() {
        return this.cuboid.isTextureMirrored();
    }

    public float getOpacity() {
        return this.cuboid.getOpacity();
    }

    public void translateOffset(float offsetX, float offsetY, float offsetZ) {
        float deltaX = this.getOffsetX() - offsetX;
        float deltaY = this.getOffsetY() - offsetY;
        float deltaZ = this.getOffsetZ() - offsetZ;
        this.cuboid.setPosition(this.getPositionX() + deltaX, this.getPositionY() + deltaY,  this.getPositionZ() + deltaZ);
    }

    public boolean hasParent() {
        return ModelHandler.INSTANCE.getParent(this.modelWrapper, this) != null;
    }
}
