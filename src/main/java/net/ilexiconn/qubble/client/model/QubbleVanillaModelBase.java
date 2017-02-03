package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
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

@SideOnly(Side.CLIENT)
public class QubbleVanillaModelBase {
    private BlockModelWrapper model;
    private List<QubbleVanillaModelRenderer> rootCubes = new ArrayList<>();
    private Map<Integer, BlockCuboidWrapper> selectionIDs = new HashMap<>();
    private Map<BlockCuboidWrapper, QubbleVanillaModelRenderer> cubes = new HashMap<>();
    private int selectionID;

    public QubbleVanillaModelBase(BlockModelWrapper wrapper) {
        this.model = wrapper;
        for (BlockCuboidWrapper cuboidWrapper : wrapper.getCuboids()) {
            this.parseCube(cuboidWrapper);
        }
    }

    private void parseCube(BlockCuboidWrapper wrapper) {
        QubbleVanillaModelRenderer box = this.createCube(wrapper);
        this.rootCubes.add(box);
    }

    private QubbleVanillaModelRenderer createCube(BlockCuboidWrapper wrapper) {
        QubbleVanillaModelRenderer box = new QubbleVanillaModelRenderer(this.selectionID, this.model, wrapper);
        this.cubes.put(wrapper, box);
        this.selectionIDs.put(this.selectionID, wrapper);
        this.selectionID++;
        return box;
    }

    public void render(float scale, boolean selection) {
        for (QubbleVanillaModelRenderer cube : this.rootCubes) {
            cube.render(scale, selection);
        }
    }

    public void renderSelectedOutline(QubbleVanillaModelRenderer selected, float scale) {
        GlStateManager.pushMatrix();
        int accent = LLibrary.CONFIG.getAccentColor();
        float r = (float) (accent >> 16 & 0xFF) / 255.0F;
        float g = (float) (accent >> 8 & 0xFF) / 255.0F;
        float b = (float) (accent & 0xFF) / 255.0F;
        GlStateManager.color(r, g, b, 1.0F);
        this.renderOutline(selected, scale);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderOutline(QubbleVanillaModelRenderer modelRenderer, float scale) {
        BlockCuboidWrapper wrapper = modelRenderer.getCuboidWrapper();
        QubbleVanillaCuboid cuboid = wrapper.getCuboid();
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.glLineWidth(16.0F);
        VertexBuffer buffer = tessellator.getBuffer();
        modelRenderer.postRender(scale);
        GlStateManager.scale(scale, scale, scale);
        float sizeX = cuboid.getToX() - cuboid.getFromX();
        float sizeY = cuboid.getToY() - cuboid.getFromY();
        float sizeZ = cuboid.getToZ() - cuboid.getFromZ();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(0.0F, 0.0F, 0.0F).endVertex();
        buffer.pos(sizeX, 0.0F, 0.0F).endVertex();
        buffer.pos(sizeX, 0.0F, sizeZ).endVertex();
        buffer.pos(0.0F, 0.0F, sizeZ).endVertex();
        buffer.pos(0.0F, 0.0F, 0.0F).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(0.0F, sizeY, 0.0F).endVertex();
        buffer.pos(sizeX, sizeY, 0.0F).endVertex();
        buffer.pos(sizeX, sizeY, sizeZ).endVertex();
        buffer.pos(0.0F, sizeY, sizeZ).endVertex();
        buffer.pos(0.0F, sizeY, 0.0F).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(0.0F, 0.0F, 0.0F).endVertex();
        buffer.pos(0.0F, sizeY, 0.0F).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(sizeX, 0.0F, 0.0F).endVertex();
        buffer.pos(sizeX, sizeY, 0.0F).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(0.0F, 0.0F, sizeZ).endVertex();
        buffer.pos(0.0F, sizeY, sizeZ).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(sizeX, 0.0F, sizeZ).endVertex();
        buffer.pos(sizeX, sizeY, sizeZ).endVertex();
        tessellator.draw();
    }

    public BlockCuboidWrapper getCuboid(int id) {
        return this.selectionIDs.get(id);
    }

    public QubbleVanillaModelRenderer getCuboid(BlockCuboidWrapper cube) {
        return this.cubes.get(cube);
    }
}
