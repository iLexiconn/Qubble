package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.qubble.Qubble;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class QubbleModelBase extends AdvancedModelBase {
    private QubbleModel model;
    private List<QubbleModelRenderer> rootCubes = new ArrayList<>();
    private int id;
    private Map<Integer, QubbleModelRenderer> ids = new HashMap<>();

    public QubbleModelBase(QubbleModel model, boolean selection) {
        this.textureWidth = model.getTextureWidth();
        this.textureHeight = model.getTextureHeight();
        this.model = model;
        for (QubbleCube cube : model.getCubes()) {
            this.parseCube(cube, null, selection);
        }
    }

    private void parseCube(QubbleCube cube, QubbleModelRenderer parent, boolean selection) {
        QubbleModelRenderer box = this.createCube(cube, selection);
        if (parent != null) {
            parent.addChild(box);
        } else {
            this.rootCubes.add(box);
        }
        for (QubbleCube child : cube.getChildren()) {
            this.parseCube(child, box, selection);
        }
    }

    private QubbleModelRenderer createCube(QubbleCube cube, boolean selection) {
        QubbleModelRenderer box = new QubbleModelRenderer(this, cube.getName(), cube.getTextureX(), cube.getTextureY(), this.id, selection);
        box.setRotationPoint(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ());
        box.addBox(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ(), cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ(), 0.0F);
        box.rotateAngleX = (float) Math.toRadians(cube.getRotationX());
        box.rotateAngleY = (float) Math.toRadians(cube.getRotationY());
        box.rotateAngleZ = (float) Math.toRadians(cube.getRotationZ());
        this.ids.put(this.id, box);
        this.id++;
        return box;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        GlStateManager.pushMatrix();
        for (QubbleModelRenderer cube : this.rootCubes) {
            cube.render(scale);
        }
        GlStateManager.popMatrix();
    }

    public void renderSelectedOutline(QubbleModelRenderer selected, float scale) {
        GlStateManager.pushMatrix();
        selected.parentedPostRender(scale);
        int accent = Qubble.CONFIG.getAccentColor();
        float r = (float) (accent >> 16 & 0xFF) / 255.0F;
        float g = (float) (accent >> 8 & 0xFF) / 255.0F;
        float b = (float) (accent & 0xFF) / 255.0F;
        GlStateManager.color(r, g, b, 1.0F);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.scale(scale, scale, scale);
        renderOutline(selected);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (selected.getParent() != null) {
            selected.getParent().parentedPostRender(scale);
        }
        selected.render(scale);
        GlStateManager.popMatrix();
    }

    private void renderOutline(QubbleModelRenderer modelRenderer) {
        ModelBox box = modelRenderer.cubeList.get(0);
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.glLineWidth(8.0F);
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

    public QubbleModelRenderer getBox(int id) {
        return this.ids.get(id);
    }
}
