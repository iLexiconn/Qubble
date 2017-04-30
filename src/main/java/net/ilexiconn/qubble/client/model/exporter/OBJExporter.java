package net.ilexiconn.qubble.client.model.exporter;

import net.ilexiconn.llibrary.client.model.obj.Face;
import net.ilexiconn.llibrary.client.model.obj.OBJModel;
import net.ilexiconn.llibrary.client.model.obj.Shape;
import net.ilexiconn.llibrary.client.model.obj.TextureCoords;
import net.ilexiconn.llibrary.client.model.obj.Vertex;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class OBJExporter implements IModelExporter<OBJModel, DefaultCuboidWrapper, DefaultModelWrapper> {
    @Override
    public String getName() {
        return "OBJ";
    }

    @Override
    public String getExtension() {
        return "obj";
    }

    @Override
    public OBJModel export(DefaultModelWrapper model, String... arguments) {
        OBJModel obj = new OBJModel();
        model.unparent();
        model.getCuboids().stream().map(cube -> this.convertBoxToShape(obj, model, cube, 0.0625F)).forEach(obj::addShape);
        return obj;
    }

    @Override
    public void save(OBJModel model, File file) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.append(model.toString());
        writer.close();
    }

    public Shape convertBoxToShape(OBJModel obj, DefaultModelWrapper model, DefaultCuboidWrapper cube, float scale) {
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
            shape.addFace(new Face(shape).append(frontBottomLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX())).append(frontBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX())).append(frontTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX())).append(frontTopLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ(), cube.getDimensionX())));
            shape.addFace(new Face(shape).append(backBottomRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX())).append(backBottomLeft, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX() * 2.0F, cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX())).append(backTopLeft, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX() * 2.0F, cube.getDimensionZ(), -cube.getDimensionX())).append(backTopRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ(), cube.getDimensionX())));
        }
        if (cube.getDimensionX() > 0.0F && cube.getDimensionZ() > 0.0F) {
            shape.addFace(new Face(shape).append(frontTopLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ(), cube.getDimensionX())).append(frontTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX())).append(backTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), 0.0F, -cube.getDimensionX())).append(backTopLeft, this.createUV(model, cube, cube.getDimensionZ(), 0.0F, cube.getDimensionX())));
            shape.addFace(new Face(shape).append(backBottomLeft, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), cube.getDimensionX())).append(backBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX() * 2.0F, cube.getDimensionZ(), -cube.getDimensionX())).append(frontBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX() * 2.0F, 0.0F, -cube.getDimensionX())).append(frontBottomLeft, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), 0.0F, cube.getDimensionX())));
        }
        if (cube.getDimensionY() > 0.0F && cube.getDimensionZ() > 0.0F) {
            shape.addFace(new Face(shape).append(backBottomLeft, this.createUV(model, cube, 0.0F, cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX() + cube.getDimensionZ() * 2.0F)).append(frontBottomLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ() + cube.getDimensionY(), cube.getDimensionX())).append(frontTopLeft, this.createUV(model, cube, cube.getDimensionZ(), cube.getDimensionZ(), cube.getDimensionX())).append(backTopLeft, this.createUV(model, cube, 0.0F, cube.getDimensionZ(), cube.getDimensionX() + cube.getDimensionZ() * 2.0F)));
            shape.addFace(new Face(shape).append(frontBottomRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX())).append(backBottomRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ() + cube.getDimensionY(), -cube.getDimensionX() - cube.getDimensionZ() * 2.0F)).append(backTopRight, this.createUV(model, cube, cube.getDimensionZ() * 2.0F + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX() - cube.getDimensionZ() * 2.0F)).append(frontTopRight, this.createUV(model, cube, cube.getDimensionZ() + cube.getDimensionX(), cube.getDimensionZ(), -cube.getDimensionX())));
        }
        shape.rotate(-cube.getRotationX(), 1.0F, 0.0F, 0.0F);
        shape.rotate(-cube.getRotationY(), 0.0F, 1.0F, 0.0F);
        shape.rotate(-cube.getRotationZ(), 0.0F, 0.0F, 1.0F);
        shape.translate(new Vector3f(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ()));
        shape.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        shape.scale(new Vector3f(scale, scale, scale));
        return shape;
    }

    public TextureCoords createUV(DefaultModelWrapper qubble, DefaultCuboidWrapper cube, float baseU, float baseV, float mirrorOffset) {
        if (!cube.isTextureMirrored()) {
            return new TextureCoords((cube.getTextureX() + baseU) / qubble.getTextureWidth(), 1.0F - (cube.getTextureY() + baseV) / qubble.getTextureHeight());
        } else {
            return new TextureCoords((cube.getTextureX() + baseU + mirrorOffset) / qubble.getTextureWidth(), 1.0F - (cube.getTextureY() + baseV) / qubble.getTextureHeight());
        }
    }

    @Override
    public String[] getArgumentNames() {
        return new String[]{};
    }

    @Override
    public String[] getDefaultArguments(DefaultModelWrapper model) {
        return new String[]{};
    }

    @Override
    public String getFileName(String[] arguments, String fileName) {
        return fileName;
    }

    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.DEFAULT;
    }
}
