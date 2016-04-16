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
    private int version;
    private int textureWidth;
    private int textureHeight;
    private List<QubbleCube> cubes = new ArrayList<>();

    public QubbleModel() {

    }

    public QubbleModel(String name, String author, int version, int textureWidth, int textureHeight, List<QubbleCube> cubes) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.cubes = cubes;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("name", this.name);
        compound.setString("author", this.author);
        compound.setInteger("version", this.version);
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
        this.version = compound.getInteger("version");
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

    public int getVersion() {
        return version;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setTexture(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
    }
}
