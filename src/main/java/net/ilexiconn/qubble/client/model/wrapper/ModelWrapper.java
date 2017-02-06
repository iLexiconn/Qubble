package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.client.model.VoxelModel;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.model.ModelType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelWrapper<CBE extends CuboidWrapper<CBE>> {
    public static final Minecraft MC = Minecraft.getMinecraft();
    public static final VoxelModel ROTATION_POINT = new VoxelModel();
    private Map<String, ModelTexture> textures = new LinkedHashMap<>();

    public abstract boolean reparent(CBE cuboid, CBE parent, boolean inPlace);

    public abstract boolean supportsParenting();

    public abstract void rebuildModel();

    public abstract void rebuildCuboid(CBE cuboid);

    public abstract void addCuboid(CBE cuboid);

    public abstract void deleteCuboid(CBE cuboid);

    public abstract void render(boolean clicking);

    public abstract void renderSelection(CBE selectedCuboid, Project project, boolean hovering);

    public abstract CBE getSelected(int selectionID);

    public abstract List<CBE> getCuboids();

    public abstract String getName();

    public abstract String getFileName();

    public abstract String getAuthor();

    protected abstract ModelWrapper<CBE> copy();

    public abstract NBTTagCompound serializeNBT();

    public abstract CBE createCuboid(String name);

    public abstract ModelType getType();

    public ModelWrapper<CBE> copyModel() {
        ModelWrapper<CBE> modelWrapper = this.copy();
        for (Map.Entry<String, ModelTexture> entry : this.textures.entrySet()) {
            modelWrapper.setTexture(entry.getKey(), entry.getValue().copy());
        }
        return modelWrapper;
    }

    public void maintainParentTransformation(CBE parenting) {
        this.applyTransformation(parenting, this.getParentTransformation(parenting, true, false));
    }

    public void inheritParentTransformation(CBE parenting, CBE newParent) {
        Matrix4d matrix = this.getParentTransformationMatrix(newParent, true, false);
        matrix.invert();
        matrix.mul(this.getParentTransformationMatrix(parenting, false, false));
        float[][] parentTransformation = this.getParentTransformation(matrix);
        this.applyTransformation(parenting, parentTransformation);
    }

    public void applyTransformation(CBE parenting, float[][] parentTransformation) {
        parenting.setPosition(parentTransformation[0][0], parentTransformation[0][1], parentTransformation[0][2]);
        parenting.setRotation(parentTransformation[1][0], parentTransformation[1][1], parentTransformation[1][2]);
    }

    public CBE getParent(CBE cuboid) {
        for (CBE currentCube : this.getCuboids()) {
            CBE foundParent = this.getParent(currentCube, cuboid);
            if (foundParent != null) {
                return foundParent;
            }
        }
        return null;
    }

    public CBE getParent(CBE parent, CBE cuboid) {
        if (parent.getChildren().contains(cuboid)) {
            return parent;
        }
        for (CBE child : parent.getChildren()) {
            CBE foundParent = this.getParent(child, cuboid);
            if (foundParent != null) {
                return foundParent;
            }
        }
        return null;
    }

    public boolean hasChild(CBE parent, CBE child) {
        if (parent.getChildren().contains(child)) {
            return true;
        }
        for (CBE c : parent.getChildren()) {
            boolean hasChild = this.hasChild(c, child);
            if (hasChild) {
                return true;
            }
        }
        return false;
    }

    public List<CBE> getParents(CBE cube, boolean ignoreSelf) {
        CBE parent = cube;
        List<CBE> parents = new ArrayList<>();
        if (!ignoreSelf) {
            parents.add(cube);
        }
        while ((parent = this.getParent(parent)) != null) {
            parents.add(parent);
        }
        Collections.reverse(parents);
        return parents;
    }

    public float[][] getParentTransformation(CBE cube, boolean includeParents, boolean ignoreSelf) {
        return this.getParentTransformation(this.getParentTransformationMatrix(cube, includeParents, ignoreSelf));
    }

    public Matrix4d getParentTransformationMatrix(CBE cube, boolean includeParents, boolean ignoreSelf) {
        List<CBE> parentCubes = new ArrayList<>();
        if (includeParents) {
            parentCubes = this.getParents(cube, ignoreSelf);
        } else if (!ignoreSelf) {
            parentCubes.add(cube);
        }
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        Matrix4d transform = new Matrix4d();
        for (CBE child : parentCubes) {
            transform.setIdentity();
            transform.setTranslation(new Vector3d(child.getPositionX(), child.getPositionY(), child.getPositionZ()));
            matrix.mul(transform);
            transform.rotZ(child.getRotationZ() / 180 * Math.PI);
            matrix.mul(transform);
            transform.rotY(child.getRotationY() / 180 * Math.PI);
            matrix.mul(transform);
            transform.rotX(child.getRotationX() / 180 * Math.PI);
            matrix.mul(transform);
        }
        return matrix;
    }

    public float[][] getParentTransformation(Matrix4d matrix) {
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
        float rotationAngleX = (float) (this.epsilon((float) Math.atan2(sinRotationAngleX, cosRotationAngleX)) / Math.PI * 180);
        float rotationAngleY = (float) (this.epsilon((float) Math.atan2(sinRotationAngleY, cosRotationAngleY)) / Math.PI * 180);
        float rotationAngleZ = (float) (this.epsilon((float) Math.atan2(sinRotationAngleZ, cosRotationAngleZ)) / Math.PI * 180);
        return new float[][] { { this.epsilon((float) matrix.m03), this.epsilon((float) matrix.m13), this.epsilon((float) matrix.m23) }, { rotationAngleX, rotationAngleY, rotationAngleZ } };
    }

    public float epsilon(float x) {
        return x < 0 ? x > -0.0001F ? 0 : x : x < 0.0001F ? 0 : x;
    }

    public ModelTexture getTexture(String name) {
        return this.textures.get(name);
    }

    public void setTexture(String name, ModelTexture texture) {
        if (texture == null) {
            this.textures.remove(name);
        } else {
            this.textures.put(name, texture);
        }
        this.rebuildModel();
    }

    public ModelTexture getBaseTexture() {
        return this.getTexture(ModelTexture.BASE);
    }

    public ModelTexture getOverlayTexture() {
        return this.getTexture(ModelTexture.OVERLAY);
    }

    public void setBaseTexture(ModelTexture texture) {
        this.setTexture(ModelTexture.BASE, texture);
    }

    public void setOverlayTexture(ModelTexture texture) {
        this.setTexture(ModelTexture.OVERLAY, texture);
    }

    public Map<String, ModelTexture> getTextures() {
        return this.textures;
    }

    public abstract void importTextures(Map<String, ModelTexture> textures);
}
