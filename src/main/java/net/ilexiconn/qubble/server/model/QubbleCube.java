package net.ilexiconn.qubble.server.model;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class QubbleCube implements INBTSerializable<NBTTagCompound> {
    private String name;
    private List<QubbleCube> children = new ArrayList<>();
    private int dimensionX;
    private int dimensionY;
    private int dimensionZ;
    private float positionX;
    private float positionY;
    private float positionZ;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float rotationX;
    private float rotationY;
    private float rotationZ;
    private float scaleX;
    private float scaleY;
    private float scaleZ;
    private int textureX;
    private int textureY;
    private boolean textureMirrored;
    private float opacity;

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("name", this.name);
        NBTTagList childrenTag = new NBTTagList();
        for (QubbleCube cube : this.children) {
            childrenTag.appendTag(cube.serializeNBT());
        }
        compound.setTag("children", childrenTag);
        NBTTagCompound dimensionTag = new NBTTagCompound();
        dimensionTag.setInteger("x", this.dimensionX);
        dimensionTag.setInteger("y", this.dimensionY);
        dimensionTag.setInteger("z", this.dimensionZ);
        compound.setTag("dimension", dimensionTag);
        NBTTagCompound positionTag = new NBTTagCompound();
        positionTag.setFloat("x", this.positionX);
        positionTag.setFloat("y", this.positionY);
        positionTag.setFloat("z", this.positionZ);
        compound.setTag("position", positionTag);
        NBTTagCompound offsetTag = new NBTTagCompound();
        offsetTag.setFloat("x", this.offsetX);
        offsetTag.setFloat("y", this.offsetY);
        offsetTag.setFloat("z", this.offsetZ);
        compound.setTag("offset", offsetTag);
        NBTTagCompound rotationTag = new NBTTagCompound();
        offsetTag.setFloat("x", this.rotationX);
        offsetTag.setFloat("y", this.rotationY);
        offsetTag.setFloat("z", this.rotationZ);
        compound.setTag("rotation", rotationTag);
        NBTTagCompound scaleTag = new NBTTagCompound();
        offsetTag.setFloat("x", this.scaleX);
        offsetTag.setFloat("y", this.scaleY);
        offsetTag.setFloat("z", this.scaleZ);
        compound.setTag("scale", scaleTag);
        NBTTagCompound textureTag = new NBTTagCompound();
        textureTag.setInteger("x", this.textureX);
        textureTag.setInteger("y", this.textureY);
        textureTag.setBoolean("mirrored", this.textureMirrored);
        compound.setTag("texture", textureTag);
        compound.setFloat("opacity", this.opacity);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.name = compound.getString("name");
        this.children = new ArrayList<>();
        NBTTagList childrenTag = compound.getTagList("children", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < childrenTag.tagCount(); i++) {
            QubbleCube cube = new QubbleCube();
            cube.deserializeNBT(childrenTag.getCompoundTagAt(i));
            this.children.add(cube);
        }
        NBTTagCompound dimensionTag = compound.getCompoundTag("dimension");
        this.dimensionX = dimensionTag.getInteger("x");
        this.dimensionY = dimensionTag.getInteger("y");
        this.dimensionZ = dimensionTag.getInteger("z");
        NBTTagCompound positionTag = compound.getCompoundTag("position");
        this.positionX = positionTag.getFloat("x");
        this.positionY = positionTag.getFloat("y");
        this.positionZ = positionTag.getFloat("z");
        NBTTagCompound offsetTag = compound.getCompoundTag("offset");
        this.offsetX = offsetTag.getFloat("x");
        this.offsetY = offsetTag.getFloat("y");
        this.offsetZ = offsetTag.getFloat("z");
        NBTTagCompound rotationTag = compound.getCompoundTag("rotation");
        this.rotationX = rotationTag.getFloat("x");
        this.rotationY = rotationTag.getFloat("y");
        this.rotationZ = rotationTag.getFloat("z");
        NBTTagCompound scaleTag = compound.getCompoundTag("scale");
        this.scaleX = scaleTag.getFloat("x");
        this.scaleY = scaleTag.getFloat("y");
        this.scaleZ = scaleTag.getFloat("z");
        NBTTagCompound textureTag = compound.getCompoundTag("texture");
        this.textureX = textureTag.getInteger("x");
        this.textureY = textureTag.getInteger("y");
        this.textureMirrored = textureTag.getBoolean("mirrored");
        this.opacity = compound.getFloat("opacity");
    }
}
