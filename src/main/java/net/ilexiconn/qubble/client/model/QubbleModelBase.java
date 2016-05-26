package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;

@SideOnly(Side.CLIENT)
public class QubbleModelBase extends AdvancedModelBase {
    private List<QubbleModelRenderer> rootCubes = new ArrayList<>();
    private Map<Integer, QubbleCuboid> ids = new HashMap<>();
    private Map<QubbleCuboid, QubbleModelRenderer> cubes = new HashMap<>();
    private int id;

    public QubbleModelBase(QubbleModel model) {
        this.textureWidth = model.getTextureWidth();
        this.textureHeight = model.getTextureHeight();
        for (QubbleCuboid cube : model.getCuboids()) {
            this.parseCube(cube, null);
        }
    }

    private void parseCube(QubbleCuboid cube, QubbleModelRenderer parent) {
        QubbleModelRenderer box = this.createCube(cube);
        if (parent != null) {
            parent.addChild(box);
        } else {
            this.rootCubes.add(box);
        }
        for (QubbleCuboid child : cube.getChildren()) {
            this.parseCube(child, box);
        }
    }

    private QubbleModelRenderer createCube(QubbleCuboid cube) {
        QubbleModelRenderer box = new QubbleModelRenderer(this, cube.getName(), cube.getTextureX(), cube.getTextureY(), this.id);
        box.setRotationPoint(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ());
        box.addBox(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ(), cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ(), 0.0F);
        box.rotateAngleX = (float) Math.toRadians(cube.getRotationX());
        box.rotateAngleY = (float) Math.toRadians(cube.getRotationY());
        box.rotateAngleZ = (float) Math.toRadians(cube.getRotationZ());
        box.mirror = cube.isTextureMirrored();
        this.cubes.put(cube, box);
        this.ids.put(this.id, cube);
        this.id++;
        return box;
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, boolean selection) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        GlStateManager.pushMatrix();
        for (QubbleModelRenderer cube : this.rootCubes) {
            cube.render(scale, selection);
        }
        GlStateManager.popMatrix();
    }

    public void renderSelectedOutline(QubbleModelRenderer selected, float scale) {
        GlStateManager.pushMatrix();
        selected.parentedPostRender(scale);
        int accent = LLibrary.CONFIG.getAccentColor();
        float r = (float) (accent >> 16 & 0xFF) / 255.0F;
        float g = (float) (accent >> 8 & 0xFF) / 255.0F;
        float b = (float) (accent & 0xFF) / 255.0F;
        GlStateManager.color(r, g, b, 1.0F);
        GlStateManager.scale(scale, scale, scale);
        this.renderOutline(selected);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderOutline(QubbleModelRenderer modelRenderer) {
        ModelBox box = modelRenderer.cubeList.get(0);
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.glLineWidth(16.0F);
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(box.posX1, box.posY1, box.posZ1).endVertex();
        buffer.pos(box.posX2, box.posY1, box.posZ1).endVertex();
        buffer.pos(box.posX2, box.posY1, box.posZ2).endVertex();
        buffer.pos(box.posX1, box.posY1, box.posZ2).endVertex();
        buffer.pos(box.posX1, box.posY1, box.posZ1).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(box.posX1, box.posY2, box.posZ1).endVertex();
        buffer.pos(box.posX2, box.posY2, box.posZ1).endVertex();
        buffer.pos(box.posX2, box.posY2, box.posZ2).endVertex();
        buffer.pos(box.posX1, box.posY2, box.posZ2).endVertex();
        buffer.pos(box.posX1, box.posY2, box.posZ1).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(box.posX1, box.posY1, box.posZ1).endVertex();
        buffer.pos(box.posX1, box.posY2, box.posZ1).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(box.posX2, box.posY1, box.posZ1).endVertex();
        buffer.pos(box.posX2, box.posY2, box.posZ1).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(box.posX1, box.posY1, box.posZ2).endVertex();
        buffer.pos(box.posX1, box.posY2, box.posZ2).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(box.posX2, box.posY1, box.posZ2).endVertex();
        buffer.pos(box.posX2, box.posY2, box.posZ2).endVertex();
        tessellator.draw();
    }

    public QubbleCuboid getCube(int id) {
        return this.ids.get(id);
    }

    public QubbleModelRenderer getCube(QubbleCuboid cube) {
        return this.cubes.get(cube);
    }

    public Set<Map.Entry<QubbleCuboid, QubbleModelRenderer>> getCubes() {
        return this.cubes.entrySet();
    }
}
