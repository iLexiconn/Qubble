package net.ilexiconn.qubble.server.model.exporter;

import com.google.common.collect.Lists;
import net.ilexiconn.qubble.server.model.QubbleCube;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.ilexiconn.qubble.server.model.obj.*;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OBJExporter implements IModelExporter<OBJModel> {
    @Override
    public String getExtension() {
        return "obj";
    }

    @Override
    public OBJModel export(QubbleModel model, String... arguments) {
        OBJModel obj = new OBJModel();
        List<QubbleCube> cubes = new ArrayList<>();
        for (QubbleCube cube : model.getCubes()) {
            cubes.add(cube);
            this.addChildCubes(cube.getChildren(), Lists.newArrayList(cube), cubes);
        }
        obj.shapes.addAll(cubes.stream().map(cube -> this.convertBoxToShape(obj, model, cube, 0.0625F)).collect(Collectors.toList()));
        return obj;
    }

    @Override
    public void save(OBJModel model, File file) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        model.shapes.forEach(shape -> shape.toStringList().forEach(writer::println));
        writer.close();
    }

    public void addChildCubes(List<QubbleCube> cubes, List<QubbleCube> parents, List<QubbleCube> list) {
        for (QubbleCube cube : cubes) {
            list.add(new QubbleCube(cube.getName(), cube.getChildren(), this.getDimension(cube), this.getPosition(cube, parents), this.getOffset(cube, parents), this.getRotation(cube, parents), this.getScale(cube, parents), this.getTexture(cube), cube.isTextureMirrored(), cube.getOpacity()));
            parents.add(cube);
            this.addChildCubes(cube.getChildren(), parents, list);
        }
    }

    public Shape convertBoxToShape(OBJModel obj, QubbleModel model, QubbleCube cube, float scale) {
        Shape shape = new Shape(obj, cube.getName());
        Vertex frontTopLeft = new Vertex(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ());
        Vertex frontTopRight = new Vertex(cube.getOffsetX() + cube.getDimensionX(), cube.getOffsetY(), cube.getOffsetZ());
        Vertex frontBottomRight = new Vertex(cube.getOffsetX() + cube.getDimensionX(), cube.getOffsetY() + cube.getDimensionY(), cube.getOffsetZ());
        Vertex frontBottomLeft = new Vertex(cube.getOffsetX(), cube.getOffsetY() + cube.getDimensionY(), cube.getOffsetZ());
        Vertex backTopLeft = new Vertex(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ() + cube.getDimensionZ());
        Vertex backTopRight = new Vertex(cube.getOffsetX() + cube.getDimensionX(), cube.getOffsetY(), cube.getOffsetZ() + cube.getDimensionZ());
        Vertex backBottomRight = new Vertex(cube.getOffsetX() + cube.getDimensionX(), cube.getOffsetY() + cube.getDimensionY(), cube.getOffsetZ() + cube.getDimensionZ());
        Vertex backBottomLeft = new Vertex(cube.getOffsetX(), cube.getOffsetY() + cube.getDimensionY(), cube.getOffsetZ() + cube.getDimensionZ());
        if (cube.getDimensionX() > 0.0F && cube.getDimensionY() > 0.0F) {
            shape.faces.add(new Face(shape).append(frontBottomLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX())).append(frontBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX())).append(frontTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX())).append(frontTopLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ(), cube.getDimensionX())));
            shape.faces.add(new Face(shape).append(backBottomRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX())).append(backBottomLeft, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX() * 2.0F, cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX())).append(backTopLeft, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX() * 2.0F, cube.getDimensionZ(), -cube.getDimensionX())).append(backTopRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ(), cube.getDimensionX())));
        }
        if (cube.getDimensionX() > 0.0F && cube.getDimensionZ() > 0.0F) {
            shape.faces.add(new Face(shape).append(frontTopLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ(), cube.getDimensionX())).append(frontTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX())).append(backTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), 0.0F, -cube.getDimensionX())).append(backTopLeft, this.createUV(model, cube, cube.getDimensionZ(), 0.0F, cube.getDimensionX())));
            shape.faces.add(new Face(shape).append(backBottomLeft, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), cube.getDimensionX())).append(backBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX() * 2.0F, cube.getDimensionZ(), -cube.getDimensionX())).append(frontBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX() * 2.0F, 0.0F, -cube.getDimensionX())).append(frontBottomLeft, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), 0.0F, cube.getDimensionX())));
        }
        if (cube.getDimensionY() > 0.0F && cube.getDimensionZ() > 0.0F) {
            shape.faces.add(new Face(shape).append(backBottomLeft, this.createUV(model, cube, 0.0F, cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX() + cube.getDimensionZ() * 2.0F)).append(frontBottomLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX())).append(frontTopLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ(), cube.getDimensionX())).append(backTopLeft, this.createUV(model, cube, 0.0F, cube.getDimensionZ(), cube.getDimensionX() + cube.getDimensionZ() * 2.0F)));
            shape.faces.add(new Face(shape).append(frontBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX())).append(backBottomRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX() - cube.getDimensionZ() * 2.0F)).append(backTopRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX() - cube.getDimensionZ() * 2.0F)).append(frontTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX())));
        }
        shape.rotate(-cube.getRotationX(), 1.0F, 0.0F, 0.0F);
        shape.rotate(-cube.getRotationY(), 0.0F, 1.0F, 0.0F);
        shape.rotate(-cube.getRotationZ(), 0.0F, 0.0F, 1.0F);
        shape.translate(new Vector3f(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ()));
        shape.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        shape.scale(new Vector3f(scale, scale, scale));
        return shape;
    }

    public TextureCoords createUV(QubbleModel qubble, QubbleCube cube, float baseU, float baseV, float mirrorOffset) {
        if (!cube.isTextureMirrored()) {
            return new TextureCoords((cube.getTextureX() + baseU) / qubble.getTextureWidth(), 1.0F - (cube.getTextureY() + baseV) / qubble.getTextureHeight());
        } else {
            return new TextureCoords((cube.getTextureX() + baseU + mirrorOffset) / qubble.getTextureWidth(), 1.0F - (cube.getTextureY() + baseV) / qubble.getTextureHeight());
        }
    }

    public int[] getDimension(QubbleCube cube) {
        return new int[] {cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ()};
    }

    public float[] getPosition(QubbleCube cube, List<QubbleCube> parents) {
        float positionX = cube.getOffsetX();
        float positionY = cube.getOffsetY();
        float positionZ = cube.getOffsetZ();
        for (QubbleCube parent : parents) {
            positionX += parent.getPositionX();
            positionY += cube.getPositionY();
            positionZ += cube.getPositionZ();
        }
        return new float[] {positionX, positionY, positionZ};
    }

    public float[] getOffset(QubbleCube cube, List<QubbleCube> parents) {
        float offsetX = cube.getOffsetX();
        float offsetY = cube.getOffsetY();
        float offsetZ = cube.getOffsetZ();
        for (QubbleCube parent : parents) {
            offsetX += parent.getOffsetX();
            offsetY += cube.getOffsetY();
            offsetZ += cube.getOffsetZ();
        }
        return new float[] {offsetX, offsetY, offsetZ};
    }

    public float[] getRotation(QubbleCube cube, List<QubbleCube> parents) {
        float rotationX = cube.getRotationX();
        float rotationY = cube.getRotationY();
        float rotationZ = cube.getRotationZ();
        for (QubbleCube parent : parents) {
            rotationX += parent.getRotationX();
            rotationY += cube.getRotationY();
            rotationZ += cube.getRotationZ();
        }
        return new float[] {rotationX, rotationY, rotationZ};
    }

    public float[] getScale(QubbleCube cube, List<QubbleCube> parents) {
        float scaleX = cube.getScaleX();
        float scaleY = cube.getScaleY();
        float scaleZ = cube.getScaleZ();
        for (QubbleCube parent : parents) {
            scaleX += parent.getScaleX();
            scaleY += cube.getScaleY();
            scaleZ += cube.getScaleZ();
        }
        return new float[] {scaleX, scaleY, scaleZ};
    }

    public int[] getTexture(QubbleCube cube) {
        return new int[] {cube.getTextureX(), cube.getTextureY()};
    }
}

