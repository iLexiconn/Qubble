package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.List;

public class BlockCuboidWrapper implements CuboidWrapper<BlockCuboidWrapper> {
    private BlockModelWrapper modelWrapper;
    private QubbleVanillaCuboid cuboid;

    public BlockCuboidWrapper(BlockModelWrapper modelWrapper, QubbleVanillaCuboid cuboid) {
        this.modelWrapper = modelWrapper;
        this.cuboid = cuboid;
    }

    @Override
    public void setName(String name) {
        this.cuboid.setName(name);
    }

    @Override
    public String getName() {
        return this.cuboid.getName();
    }

    @Override
    public List<BlockCuboidWrapper> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public BlockCuboidWrapper copy(ModelWrapper<BlockCuboidWrapper> model) {
        BlockModelWrapper defaultModel = (BlockModelWrapper) model;
        return new BlockCuboidWrapper(defaultModel, ModelHandler.INSTANCE.copy(defaultModel, this));
    }

    @Override
    public BlockCuboidWrapper copyRaw() {
        return new BlockCuboidWrapper(this.modelWrapper, this.cuboid.copy());
    }

    @Override
    public float getPositionX() {
        return this.cuboid.getFromX();
    }

    @Override
    public float getPositionY() {
        return this.cuboid.getFromY();
    }

    @Override
    public float getPositionZ() {
        return this.cuboid.getFromZ();
    }

    @Override
    public float getOffsetX() {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();
        if (rotation != null) {
            return rotation.getOriginX();
        }
        return 0;
    }

    @Override
    public float getOffsetY() {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();
        if (rotation != null) {
            return rotation.getOriginY();
        }
        return 0;
    }

    @Override
    public float getOffsetZ() {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();
        if (rotation != null) {
            return rotation.getOriginZ();
        }
        return 0;
    }

    @Override
    public float getRotationX() {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();
        if (rotation != null && rotation.getAxis() == EnumFacing.Axis.X) {
            return rotation.getAngle();
        }
        return 0;
    }

    @Override
    public float getRotationY() {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();
        if (rotation != null && rotation.getAxis() == EnumFacing.Axis.Y) {
            return rotation.getAngle();
        }
        return 0;
    }

    @Override
    public float getRotationZ() {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();
        if (rotation != null && rotation.getAxis() == EnumFacing.Axis.Z) {
            return rotation.getAngle();
        }
        return 0;
    }

    @Override
    public float getScaleX() {
        return 1.0F;
    }

    @Override
    public float getScaleY() {
        return 1.0F;
    }

    @Override
    public float getScaleZ() {
        return 1.0F;
    }

    @Override
    public float getDimensionX() {
        return this.cuboid.getToX() - this.cuboid.getFromX();
    }

    @Override
    public float getDimensionY() {
        return this.cuboid.getToY() - this.cuboid.getFromY();
    }

    @Override
    public float getDimensionZ() {
        return this.cuboid.getToZ() - this.cuboid.getFromZ();
    }

    @Override
    public float getMinU(EnumFacing facing) {
        return this.cuboid.getFace(facing).getMinU();
    }

    @Override
    public float getMinV(EnumFacing facing) {
        return this.cuboid.getFace(facing).getMinV();
    }

    @Override
    public float getMaxU(EnumFacing facing) {
        return this.cuboid.getFace(facing).getMaxU();
    }

    @Override
    public float getMaxV(EnumFacing facing) {
        return this.cuboid.getFace(facing).getMaxV();
    }

    @Override
    public int getTextureX() {
        return 0;
    }

    @Override
    public int getTextureY() {
        return 0;
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        float dimensionX = this.getDimensionX();
        float dimensionY = this.getDimensionY();
        float dimensionZ = this.getDimensionZ();
        float offsetX = this.getOffsetX();
        float offsetY = this.getOffsetY();
        float offsetZ = this.getOffsetZ();
        this.cuboid.setFrom(x, y, z);
        this.cuboid.setTo(x + dimensionX, y + dimensionY, z + dimensionZ);
        this.setOffset(offsetX, offsetY, offsetZ);
    }

    @Override
    public void setOffset(float x, float y, float z) {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();
        if (rotation == null) {
            rotation = QubbleVanillaRotation.create(EnumFacing.Axis.X, x, y, z, 0.0F);
            this.cuboid.setRotation(rotation);
        } else {
            rotation.setOrigin(x, y, z);
        }
    }

    @Override
    public void setRotation(float x, float y, float z) {
        EnumFacing.Axis axis = null;
        float angle = 0.0F;
        if (z != 0.0F) {
            axis = EnumFacing.Axis.Z;
            angle = z;
        } else if (y != 0.0F) {
            axis = EnumFacing.Axis.Y;
            angle = y;
        } else if (x != 0.0F) {
            axis = EnumFacing.Axis.X;
            angle = x;
        }
        if (axis != null) {
            QubbleVanillaRotation rotation = QubbleVanillaRotation.create(axis, this.getPositionX(), this.getPositionY(), this.getPositionZ(), angle);
            this.cuboid.setRotation(rotation);
        } else {
            this.cuboid.setRotation(null);
        }
    }

    @Override
    public void setScale(float x, float y, float z) {
    }

    @Override
    public void setDimensions(float x, float y, float z) {
        float deltaX = x - this.getDimensionX();
        float deltaY = y - this.getDimensionY();
        float deltaZ = z - this.getDimensionZ();
        for (EnumFacing facing : EnumFacing.VALUES) {
            QubbleVanillaFace face = this.cuboid.getFace(facing);
            EnumFacing.Axis axis = facing.getAxis();
            float deltaU = deltaX;
            float deltaV = deltaY;
            if (axis == EnumFacing.Axis.Y) {
                deltaV = deltaZ;
            } else if (axis == EnumFacing.Axis.X) {
                deltaU = deltaZ;
            }
            this.setMaxU(facing, face.getMaxU() + deltaU);
            this.setMaxV(facing, face.getMaxV() + deltaV);
        }
        this.cuboid.setTo(this.cuboid.getFromX() + x, this.cuboid.getFromY() + y, this.cuboid.getFromZ() + z);
    }

    @Override
    public void setTexture(float x, float y) {
    }

    @Override
    public void setMinU(EnumFacing facing, float minU) {
        this.cuboid.getFace(facing).setMinU(MathHelper.clamp(minU, 0.0F, 16.0F));
    }

    @Override
    public void setMinV(EnumFacing facing, float minV) {
        this.cuboid.getFace(facing).setMinV(MathHelper.clamp(minV, 0.0F, 16.0F));
    }

    @Override
    public void setMaxU(EnumFacing facing, float maxU) {
        this.cuboid.getFace(facing).setMaxU(MathHelper.clamp(maxU, 0.0F, 16.0F));
    }

    @Override
    public void setMaxV(EnumFacing facing, float maxV) {
        this.cuboid.getFace(facing).setMaxV(MathHelper.clamp(maxV, 0.0F, 16.0F));
    }

    @Override
    public void setTextureMirrored(boolean mirrored) {
    }

    @Override
    public void addChild(BlockCuboidWrapper cuboid) {
    }

    @Override
    public void removeChild(BlockCuboidWrapper cuboid) {
    }

    @Override
    public boolean hasChild(BlockCuboidWrapper cuboid) {
        return false;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.BLOCK;
    }

    @Override
    public void setAutoUV() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            EnumFacing.Axis axis = facing.getAxis();
            float maxU = this.getDimensionX();
            float maxV = this.getDimensionY();
            if (axis == EnumFacing.Axis.Y) {
                maxV = this.getDimensionZ();
            } else if (axis == EnumFacing.Axis.X) {
                maxU = this.getDimensionZ();
            }
            this.setMinU(facing, 0.0F);
            this.setMinV(facing, 0.0F);
            this.setMaxU(facing, maxU);
            this.setMaxV(facing, maxV);
        }
    }

    public QubbleVanillaCuboid getCuboid() {
        return this.cuboid;
    }

    public void setShade(boolean shade) {
        this.cuboid.setShade(shade);
    }

    public boolean hasShade() {
        return this.cuboid.hasShade();
    }
}
