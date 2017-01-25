package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.client.model.VoxelModel;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.model.ModelType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ModelWrapper<CBE extends CuboidWrapper<CBE>> {
    Minecraft MC = Minecraft.getMinecraft();
    VoxelModel ROTATION_POINT = new VoxelModel();

    boolean reparent(CBE cuboid, CBE parent, boolean inPlace);

    boolean supportsParenting();

    void rebuildModel();

    void rebuildCuboid(CBE cuboid);

    void addCuboid(CBE cuboid);

    void copyCuboid(CBE cuboid);

    void deleteCuboid(CBE cuboid);

    void render(boolean clicking);

    void renderSelection(CBE selectedCuboid, Project project);

    CBE getSelected(int selectionID);

    List<CBE> getCuboids();

    String getName();

    String getFileName();

    String getAuthor();

    ModelWrapper<CBE> copy();

    NBTTagCompound serializeNBT();

    CBE createCuboid(String name);

    ModelType getType();

    default void maintainParentTransformation(CBE parenting) {
        this.applyTransformation(parenting, this.getParentTransformation(parenting, true, false));
    }

    default void inheritParentTransformation(CBE parenting, CBE newParent) {
        Matrix4d matrix = this.getParentTransformationMatrix(newParent, true, false);
        matrix.invert();
        matrix.mul(this.getParentTransformationMatrix(parenting, false, false));
        float[][] parentTransformation = this.getParentTransformation(matrix);
        this.applyTransformation(parenting, parentTransformation);
    }

    default void applyTransformation(CBE parenting, float[][] parentTransformation) {
        parenting.setPosition(parentTransformation[0][0], parentTransformation[0][1], parentTransformation[0][2]);
        parenting.setRotation(parentTransformation[1][0], parentTransformation[1][1], parentTransformation[1][2]);
    }

    default CBE getParent(CBE cuboid) {
        for (CBE currentCube : this.getCuboids()) {
            CBE foundParent = this.getParent(currentCube, cuboid);
            if (foundParent != null) {
                return foundParent;
            }
        }
        return null;
    }

    default CBE getParent(CBE parent, CBE cuboid) {
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

    default boolean hasChild(CBE parent, CBE child) {
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

    default List<CBE> getParents(CBE cube, boolean ignoreSelf) {
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

    default float[][] getParentTransformation(CBE cube, boolean includeParents, boolean ignoreSelf) {
        return this.getParentTransformation(this.getParentTransformationMatrix(cube, includeParents, ignoreSelf));
    }

    default Matrix4d getParentTransformationMatrix(CBE cube, boolean includeParents, boolean ignoreSelf) {
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

    default float[][] getParentTransformation(Matrix4d matrix) {
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

    default float epsilon(float x) {
        return x < 0 ? x > -0.0001F ? 0 : x : x < 0.0001F ? 0 : x;
    }
}
