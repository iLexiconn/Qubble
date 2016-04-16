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

    public QubbleModel unparent() {
        List<QubbleCube> unparentedCubes = new ArrayList<>();
        for (QubbleCube cube : this.cubes) {
            unparentedCubes.add(cube);
            this.addChildCubes(cube.getChildren(), cube, unparentedCubes);
        }
        this.cubes.clear();
        this.cubes.addAll(unparentedCubes);
        return this;
    }

    private void addChildCubes(List<QubbleCube> cubes, QubbleCube parent, List<QubbleCube> list) {
        for (QubbleCube cube : cubes) {
            QubbleCube newCube = new QubbleCube(cube.getName(), cube.getChildren(), this.getDimension(cube), this.getPosition(cube, parent), this.getOffset(cube, parent), this.getRotation(cube, parent), this.getScale(cube, parent), this.getTexture(cube), cube.isTextureMirrored(), cube.getOpacity());
            list.add(newCube);
            this.addChildCubes(cube.getChildren(), newCube, list);
        }
    }

    private int[] getDimension(QubbleCube cube) {
        return new int[]{cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ()};
    }

    private float[] getPosition(QubbleCube cube, QubbleCube parent) {
        float positionX = cube.getOffsetX();
        float positionY = cube.getOffsetY();
        float positionZ = cube.getOffsetZ();
        positionX += parent.getPositionX();
        positionY += cube.getPositionY();
        positionZ += cube.getPositionZ();
        return new float[]{positionX, positionY, positionZ};
    }

    private float[] getOffset(QubbleCube cube, QubbleCube parent) {
        float offsetX = cube.getOffsetX();
        float offsetY = cube.getOffsetY();
        float offsetZ = cube.getOffsetZ();
        offsetX += parent.getOffsetX();
        offsetY += cube.getOffsetY();
        offsetZ += cube.getOffsetZ();
        return new float[]{offsetX, offsetY, offsetZ};
    }

    private float[] getRotation(QubbleCube cube, QubbleCube parent) {
        float rotationX = cube.getRotationX();
        float rotationY = cube.getRotationY();
        float rotationZ = cube.getRotationZ();
        rotationX += parent.getRotationX();
        rotationY += cube.getRotationY();
        rotationZ += cube.getRotationZ();
        return new float[]{rotationX, rotationY, rotationZ};
    }

    private float[] getScale(QubbleCube cube, QubbleCube parent) {
        float scaleX = cube.getScaleX();
        float scaleY = cube.getScaleY();
        float scaleZ = cube.getScaleZ();
        scaleX += parent.getScaleX();
        scaleY += cube.getScaleY();
        scaleZ += cube.getScaleZ();
        return new float[]{scaleX, scaleY, scaleZ};
    }

    private int[] getTexture(QubbleCube cube) {
        return new int[]{cube.getTextureX(), cube.getTextureY()};
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
