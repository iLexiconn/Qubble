package net.ilexiconn.qubble.client.model.exporter;

import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ScalaExporter implements IModelExporter<List<String>, DefaultCuboidWrapper, DefaultModelWrapper> {
    @Override
    public String getName() {
        return "Scala";
    }

    @Override
    public String getExtension() {
        return "scala";
    }

    @Override
    public List<String> export(DefaultModelWrapper model, String... arguments) {
        List<String> list = new ArrayList<>();
        list.add("package " + arguments[0]);
        list.add("");
        list.add("import net.minecraft.client.model.{ModelRenderer, ModelBase}");
        list.add("import net.minecraft.client.renderer.GlStateManager");
        list.add("import net.minecraft.entity.Entity");
        list.add("import net.minecraftforge.fml.relauncher.{Side, SideOnly}");
        list.add("import org.lwjgl.opengl.GL11");
        list.add("");
        list.add("/**");
        list.add(" * " + model.getName() + " by " + model.getAuthor());
        list.add(" */");
        list.add("@SideOnly(Side.CLIENT)");
        list.add("class " + arguments[1] + " extends ModelBase {");
        list.add("  textureWidth = " + model.getTextureWidth());
        list.add("  textureHeight = " + model.getTextureHeight());
        list.add("");
        this.addCubeDeclarations(model.getCuboids(), null, list);
        list.add("");
        list.add("  override def render(entity: Entity, limbSwing: Float, limbSwingAmount: Float, ageInTick: Float, rotationYaw: Float, rotationPitch: Float, scale: Float) {");
        this.addRenderCalls(model.getCuboids(), list);
        list.add("  }");
        list.add("");
        this.addRotationAngles(list);
        list.add("}");
        return list;
    }

    @Override
    public void save(List<String> model, File file) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        model.forEach(writer::println);
        writer.close();
    }

    public void addCubeDeclarations(List<DefaultCuboidWrapper> cubes, DefaultCuboidWrapper parent, List<String> list) {
        for (DefaultCuboidWrapper cube : cubes) {
            String field = this.getFieldName(cube);
            list.add("  val " + field + " = new ModelRenderer(this, " + cube.getTextureX() + ", " + cube.getTextureY() + ")");
            list.add("  this." + field + ".setRotationPoint(" + cube.getPositionX() + "F, " + cube.getPositionY() + "F, " + cube.getPositionZ() + "F)");
            list.add("  this." + field + ".addBox(" + cube.getOffsetX() + "F, " + cube.getOffsetY() + "F, " + cube.getOffsetZ() + "F, " + cube.getDimensionX() + ", " + cube.getDimensionY() + ", " + cube.getDimensionZ() + ")");
            if (cube.isTextureMirrored()) {
                list.add("  this." + field + ".mirror = true");
            }
            if (cube.getRotationX() != 0.0F || cube.getRotationY() != 0.0F || cube.getRotationZ() != 0.0F) {
                list.add("  this.setRotationAngles(this." + field + ", " + Math.toRadians(cube.getRotationX()) + "F, " + Math.toRadians(cube.getRotationY()) + "F, " + Math.toRadians(cube.getRotationZ()) + "F)");
            }
            if (parent != null) {
                list.add("  this." + this.getFieldName(parent) + ".addChild(this." + field + ")");
            }
            this.addCubeDeclarations(cube.getChildren(), cube, list);
        }
    }

    public void addRenderCalls(List<DefaultCuboidWrapper> cubes, List<String> list) {
        for (DefaultCuboidWrapper cube : cubes) {
            String field = this.getFieldName(cube);
            boolean scale = cube.getRotationX() != 0.0F || cube.getRotationY() != 0.0F || cube.getRotationZ() != 0.0F;
            boolean blend = cube.getOpacity() != 100.0F;
            if (scale) {
                list.add("    GlStateManager.pushMatrix()");
                list.add("    GlStateManager.translate(this." + field + ".offsetX, this." + field + ".offsetY, this." + field + ".offsetZ)");
                list.add("    GlStateManager.translate(this." + field + ".rotationPointX * scale, this." + field + ".rotationPointY * scale, this." + field + ".rotationPointZ * scale)");
                list.add("    GlStateManager.scale(" + cube.getScaleX() + "F, " + cube.getScaleY() + "F, " + cube.getScaleZ() + "F)");
                list.add("    GlStateManager.translate(-this." + field + ".offsetX, -this." + field + ".offsetY, -this." + field + ".offsetZ)");
                list.add("    GlStateManager.translate(-this." + field + ".rotationPointX * scale, -this." + field + ".rotationPointY * scale, -this." + field + ".rotationPointZ * scale)");
            }
            if (blend) {
                list.add("    GlStateManager.enableBlend()");
                list.add("    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)");
                list.add("    GlStateManager.color(1.0F, 1.0F, 1.0F, " + (cube.getOpacity() / 100.0F) + "F)");
            }
            list.add("    this." + field + ".render(scale)");
            if (blend) {
                list.add("    GlStateManager.disableBlend()");
            }
            if (scale) {
                list.add("    GlStateManager.popMatrix()");
            }
        }
    }

    public void addRotationAngles(List<String> list) {
        list.add("  def setRotationAngles(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {");
        list.add("    modelRenderer.rotateAngleX = x");
        list.add("    modelRenderer.rotateAngleY = y");
        list.add("    modelRenderer.rotateAngleZ = z");
        list.add("  }");
    }

    public String getFieldName(DefaultCuboidWrapper cube) {
        return cube.getName().replaceAll("[^A-Za-z0-9_$]", "");
    }

    @Override
    public String[] getArgumentNames() {
        return new String[]{"Package", "Class Name"};
    }

    @Override
    public String[] getDefaultArguments(DefaultModelWrapper currentModel) {
        return new String[]{"pkg", currentModel.getName()};
    }

    @Override
    public String getFileName(String[] arguments, String fileName) {
        return arguments[1];
    }

    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.DEFAULT;
    }
}
