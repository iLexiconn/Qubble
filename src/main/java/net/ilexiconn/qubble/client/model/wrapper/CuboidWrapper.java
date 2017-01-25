package net.ilexiconn.qubble.client.model.wrapper;

import net.minecraft.util.EnumFacing;

import java.util.List;

public interface CuboidWrapper<CBE extends CuboidWrapper<CBE>> {
    void setName(String name);

    String getName();

    List<CBE> getChildren();

    float getPositionX();

    float getPositionY();

    float getPositionZ();

    float getOffsetX();

    float getOffsetY();

    float getOffsetZ();

    float getRotationX();

    float getRotationY();

    float getRotationZ();

    float getScaleX();

    float getScaleY();

    float getScaleZ();

    float getDimensionX();

    float getDimensionY();

    float getDimensionZ();

    float getMinU(EnumFacing facing);

    float getMinV(EnumFacing facing);

    float getMaxU(EnumFacing facing);

    float getMaxV(EnumFacing facing);

    int getTextureX();

    int getTextureY();

    void setPosition(float x, float y, float z);

    void setOffset(float x, float y, float z);

    void setRotation(float x, float y, float z);

    void setScale(float x, float y, float z);

    void setDimensions(float x, float y, float z);

    void setTexture(float x, float y);

    void setMinU(EnumFacing facing, float minU);

    void setMinV(EnumFacing facing, float minV);

    void setMaxU(EnumFacing facing, float maxU);

    void setMaxV(EnumFacing facing, float maxV);

    void addChild(CBE cuboid);

    void removeChild(CBE cuboid);

    boolean hasChild(CBE cuboid);
}
