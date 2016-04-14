package net.ilexiconn.qubble.server.model;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class QubbleModel implements INBTSerializable<NBTTagCompound> {
    private String name;
    private String author;
    private int textureWidth;
    private int textureHeight;
    private List<QubbleCube> cubes = new ArrayList<>();

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("name", this.name);
        compound.setString("author", this.author);
        NBTTagCompound textureTag = new NBTTagCompound();
        textureTag.setInteger("width", this.textureWidth);
        textureTag.setInteger("height", this.textureHeight);
        compound.setTag("texture", textureTag);
        NBTTagList cubesTag = new NBTTagList();
        for (QubbleCube cube : this.cubes) {
            cubesTag.appendTag(cube.serializeNBT());
        }
        compound.setTag("cubes", cubesTag);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.name = compound.getString("name");
        this.author = compound.getString("author");
        NBTTagCompound textureTag = compound.getCompoundTag("texture");
        this.textureWidth = textureTag.getInteger("width");
        this.textureHeight = textureTag.getInteger("height");
        this.cubes = new ArrayList<>();
        NBTTagList cubesList = compound.getTagList("cubes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < cubesList.tagCount(); i++) {
            QubbleCube cube = new QubbleCube();
            cube.deserializeNBT(cubesList.getCompoundTagAt(i));
            this.cubes.add(cube);
        }
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public List<QubbleCube> getCubes() {
        return cubes;
    }
}
