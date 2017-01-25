package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class QubbleModelBase extends AdvancedModelBase {
    private List<QubbleModelRenderer> rootCubes = new ArrayList<>();
    private Map<Integer, DefaultCuboidWrapper> selectionIDs = new HashMap<>();
    private Map<DefaultCuboidWrapper, QubbleModelRenderer> cubes = new HashMap<>();
    private int selectionID;

    public QubbleModelBase(DefaultModelWrapper wrapper) {
        QubbleModel model = wrapper.getModel();
        this.textureWidth = model.getTextureWidth();
        this.textureHeight = model.getTextureHeight();
        for (DefaultCuboidWrapper cuboidWrapper : wrapper.getCuboids()) {
            this.parseCube(cuboidWrapper, null);
        }
    }

    private void parseCube(DefaultCuboidWrapper wrapper, QubbleModelRenderer parent) {
        QubbleModelRenderer box = this.createCube(wrapper);
        if (parent != null) {
            parent.addChild(box);
        } else {
            this.rootCubes.add(box);
        }
        for (DefaultCuboidWrapper child : wrapper.getChildren()) {
            this.parseCube(child, box);
        }
    }

    private QubbleModelRenderer createCube(DefaultCuboidWrapper wrapper) {
        QubbleCuboid cuboid = wrapper.getCuboid();
        QubbleModelRenderer box = new QubbleModelRenderer(this, cuboid.getName(), cuboid.getTextureX(), cuboid.getTextureY(), this.selectionID);
        box.setRotationPoint(cuboid.getPositionX(), cuboid.getPositionY(), cuboid.getPositionZ());
        box.addBox(cuboid.getOffsetX(), cuboid.getOffsetY(), cuboid.getOffsetZ(), cuboid.getDimensionX(), cuboid.getDimensionY(), cuboid.getDimensionZ(), 0.0F);
        box.rotateAngleX = (float) Math.toRadians(cuboid.getRotationX());
        box.rotateAngleY = (float) Math.toRadians(cuboid.getRotationY());
        box.rotateAngleZ = (float) Math.toRadians(cuboid.getRotationZ());
        box.mirror = cuboid.isTextureMirrored();
        box.scaleX = cuboid.getScaleX();
        box.scaleY = cuboid.getScaleY();
        box.scaleZ = cuboid.getScaleZ();
        this.cubes.put(wrapper, box);
        this.selectionIDs.put(this.selectionID, wrapper);
        this.selectionID++;
        return box;
    }

    public void render(float scale, boolean selection) {
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

    public DefaultCuboidWrapper getCuboid(int id) {
        return this.selectionIDs.get(id);
    }

    public QubbleModelRenderer getCuboid(DefaultCuboidWrapper cube) {
        return this.cubes.get(cube);
    }

    public Set<Map.Entry<DefaultCuboidWrapper, QubbleModelRenderer>> getCubes() {
        return this.cubes.entrySet();
    }

    public void delete() {
        for (QubbleModelRenderer renderer : this.rootCubes) {
            renderer.delete();
        }
    }
}
