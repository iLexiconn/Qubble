package net.ilexiconn.qubble.server.model.qubble;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

public class QubbleModel implements INBTSerializable<NBTTagCompound> {
    private String name;
    private String author;
    private int version;
    private int textureWidth;
    private int textureHeight;
    private List<QubbleCube> cubes = new ArrayList<>();
    private List<QubbleAnimation> animations = new ArrayList<>();
    private transient String fileName;

    public QubbleModel() {
    }

    public QubbleModel(String name, String fileName, String author, int version, int textureWidth, int textureHeight, List<QubbleCube> cubes, List<QubbleAnimation> animations) {
        this.name = name;
        this.fileName = fileName;
        this.author = author;
        this.version = version;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.cubes = cubes;
        this.animations = animations;
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
        NBTTagList animationsTag = new NBTTagList();
        for (QubbleAnimation animation : this.animations) {
            cubesTag.appendTag(animation.serializeNBT());
        }
        compound.setTag("animations", animationsTag);
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
        NBTTagList cubesTag = compound.getTagList("cubes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < cubesTag.tagCount(); i++) {
            QubbleCube cube = new QubbleCube();
            cube.deserializeNBT(cubesTag.getCompoundTagAt(i));
            this.cubes.add(cube);
        }
        NBTTagList animationsTag = compound.getTagList("animations", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < animationsTag.tagCount(); i++) {
            QubbleAnimation animation = new QubbleAnimation();
            animation.deserializeNBT(cubesTag.getCompoundTagAt(i));
            this.animations.add(animation);
        }
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
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

    public List<QubbleAnimation> getAnimations() {
        return animations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public QubbleModel unparent() {
        List<QubbleCube> unparentedCubes = new ArrayList<>();
        for (QubbleCube cube : this.cubes) {
            List<QubbleCube> parentCubes = new ArrayList<>();
            parentCubes.add(cube);
            unparentedCubes.add(cube);
            this.unparentCubes(new ArrayList<>(cube.getChildren()), unparentedCubes, parentCubes);
        }
        this.cubes.clear();
        this.cubes.addAll(unparentedCubes);
        return this;
    }

    private void unparentCubes(List<QubbleCube> cubes, List<QubbleCube> childCubes, List<QubbleCube> parentCubes) {
        for (QubbleCube cube : cubes) {
            List<QubbleCube> newParentCubes = new ArrayList<>(parentCubes);
            newParentCubes.add(cube);
            float[][] transformation = this.getParentTransformation(newParentCubes);
            cube = new QubbleCube(cube.getName(), new ArrayList<>(cube.getChildren()), new int[]{cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ()}, transformation[0], new float[]{cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ()}, transformation[1], new float[]{cube.getScaleX(), cube.getScaleY(), cube.getScaleZ()}, new int[]{cube.getTextureX(), cube.getTextureY()}, cube.isTextureMirrored(), cube.getOpacity());
            childCubes.add(cube);
            this.unparentCubes(cube.getChildren(), childCubes, new ArrayList<>(newParentCubes));
        }
    }

    private float[][] getParentTransformation(List<QubbleCube> parentCubes) {
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        Matrix4d transform = new Matrix4d();
        for (QubbleCube cube : parentCubes) {
            transform.setIdentity();
            transform.setTranslation(new Vector3d(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ()));
            matrix.mul(transform);
            transform.rotZ(cube.getRotationZ() / 180 * Math.PI);
            matrix.mul(transform);
            transform.rotY(cube.getRotationY() / 180 * Math.PI);
            matrix.mul(transform);
            transform.rotX(cube.getRotationX() / 180 * Math.PI);
            matrix.mul(transform);
        }
        double sinRotationAngleY, cosRotationAngleY, sinRotationAngleX, cosRotationAngleX, sinRotationAngleZ, cosRotationAngleZ;
        sinRotationAngleY = -matrix.m20;
        cosRotationAngleY = Math.sqrt(1 - sinRotationAngleY * sinRotationAngleY);
        if (Math.abs(cosRotationAngleY) > 0.0001) {
            sinRotationAngleX = matrix.m21 / cosRotationAngleY;
            cosRotationAngleX = matrix.m22 / cosRotationAngleY;
            sinRotationAngleZ = matrix.m10 / cosRotationAngleY;
            cosRotationAngleZ = matrix.m00 / cosRotationAngleY;
        } else {
            sinRotationAngleX = -matrix.m12;
            cosRotationAngleX = matrix.m11;
            sinRotationAngleZ = 0;
            cosRotationAngleZ = 1;
        }
        float rotationAngleX = (float) (epsilon((float) Math.atan2(sinRotationAngleX, cosRotationAngleX)) / Math.PI * 180);
        float rotationAngleY = (float) (epsilon((float) Math.atan2(sinRotationAngleY, cosRotationAngleY)) / Math.PI * 180);
        float rotationAngleZ = (float) (epsilon((float) Math.atan2(sinRotationAngleZ, cosRotationAngleZ)) / Math.PI * 180);
        return new float[][] { { epsilon((float) matrix.m03), epsilon((float) matrix.m13), epsilon((float) matrix.m23) }, { rotationAngleX, rotationAngleY, rotationAngleZ } };
    }

    private float epsilon(float x) {
        return x < 0 ? x > -0.0001F ? 0 : x : x < 0.0001F ? 0 : x;
    }

    public QubbleModel copy() {
        return new QubbleModel(this.name, this.fileName, this.author, this.version, this.textureWidth, this.textureHeight, new ArrayList<>(this.cubes), new ArrayList<>(this.animations));
    }
}
