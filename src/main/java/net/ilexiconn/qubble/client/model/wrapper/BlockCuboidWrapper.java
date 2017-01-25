package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.List;

public class BlockCuboidWrapper implements CuboidWrapper<BlockCuboidWrapper> {
    private QubbleVanillaCuboid cuboid;

    public BlockCuboidWrapper(QubbleVanillaCuboid cuboid) {
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
    public void setPosition(float x, float y, float z) {
        float dimensionX = this.getDimensionX();
        float dimensionY = this.getDimensionY();
        float dimensionZ = this.getDimensionZ();
        float offsetX = this.getOffsetX();
        float offsetY = this.getOffsetY();
        float offsetZ = this.getOffsetZ();
        this.cuboid.setFrom(x, y, z);
        this.setDimensions(dimensionX, dimensionY, dimensionZ);
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
        this.cuboid.setTo(this.cuboid.getFromX() + x, this.cuboid.getFromY() + y, this.cuboid.getFromZ() + z);
    }

    @Override
    public void setTexture(float x, float y) {
    }

    @Override
    public void setMinU(EnumFacing facing, float minU) {
        this.cuboid.getFace(facing).setMinU(minU);
    }

    @Override
    public void setMinV(EnumFacing facing, float minV) {
        this.cuboid.getFace(facing).setMinV(minV);
    }

    @Override
    public void setMaxU(EnumFacing facing, float maxU) {
        this.cuboid.getFace(facing).setMaxU(maxU);
    }

    @Override
    public void setMaxV(EnumFacing facing, float maxV) {
        this.cuboid.getFace(facing).setMaxV(maxV);
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

    public QubbleVanillaCuboid getCuboid() {
        return this.cuboid;
    }
}
