package net.ilexiconn.qubble.client.model.render;

import net.ilexiconn.llibrary.client.model.VoxelModel;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public abstract class QubbleRenderModel<CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> {
    protected static final Minecraft MC = Minecraft.getMinecraft();
    protected static final VoxelModel ROTATION_POINT = new VoxelModel();

    protected final Map<CBE, Integer> cuboidIDs = new HashMap<>();
    protected final List<CBE> cuboids = new ArrayList<>();
    protected final List<CBE> rootCuboids = new ArrayList<>();

    protected final MDL model;

    public QubbleRenderModel(MDL model) {
        this.model = model;
        this.build();
    }

    public final void build() {
        this.cuboidIDs.clear();
        this.cuboids.clear();
        int id = 0;
        for (CBE cuboid : this.model.getCuboids()) {
            id = this.build(cuboid, id);
        }
    }

    private int build(CBE cuboid, int id) {
        this.cuboidIDs.put(cuboid, id++);
        this.cuboids.add(cuboid);
        if (ModelHandler.INSTANCE.getParent(this.model, cuboid) == null) {
            this.rootCuboids.add(cuboid);
        }
        for (CBE child : cuboid.getChildren()) {
            id = this.build(child, id);
        }
        return id;
    }

    public void render(boolean selection) {
        for (CBE cuboid : this.rootCuboids) {
            this.renderCuboid(cuboid, selection);
        }
    }

    public abstract void renderCuboid(CBE cuboid, boolean selection);

    public abstract void renderCuboidSingle(CBE cuboid, boolean selection);

    public abstract void postRender(CBE cuboid);

    public void parentedPostRender(CBE cuboid) {
        List<CBE> cuboids = ModelHandler.INSTANCE.getParents(this.model, cuboid, false);
        for (CBE parent : cuboids) {
            this.postRender(parent);
        }
    }

    public abstract void renderOutline(CBE cuboid, boolean hovering);

    public abstract void renderSelection(CBE cuboid, boolean hovering);

    public final int getID(CBE cuboid) {
        Integer id = this.cuboidIDs.get(cuboid);
        return id != null ? id : 0;
    }

    public final CBE getSelectedCuboid(int id) {
        return id >= 0 && id < this.cuboids.size() ? this.cuboids.get(id) : null;
    }

    protected void drawFace(CBE cuboid, EnumFacing facing, float textureWidth, float textureHeight) {
        int textureX = cuboid.getTextureX();
        int textureY = cuboid.getTextureY();
        float normalX = facing.getFrontOffsetX();
        float normalY = facing.getFrontOffsetY();
        float normalZ = facing.getFrontOffsetZ();
        float minX = cuboid.getOffsetX() * 0.0625F;
        float minY = cuboid.getOffsetY() * 0.0625F;
        float minZ = cuboid.getOffsetZ() * 0.0625F;
        float dimensionX = cuboid.getDimensionX();
        float dimensionY = cuboid.getDimensionY();
        float dimensionZ = cuboid.getDimensionZ();
        float maxX = minX + dimensionX * 0.0625F;
        float maxY = minY + dimensionY * 0.0625F;
        float maxZ = minZ + dimensionZ * 0.0625F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        if (facing == EnumFacing.UP) {
            float minU = textureX + dimensionZ;
            float maxU = minU + dimensionX;
            float minV = textureY;
            float maxV = minV + dimensionZ;
            buffer.pos(minX, minY, minZ).tex(minU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, minZ).tex(maxU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, maxZ).tex(maxU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, minY, maxZ).tex(minU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.DOWN) {
            float minU = textureX + dimensionZ + dimensionX;
            float maxU = minU + dimensionX;
            float minV = textureY;
            float maxV = minV + dimensionZ;
            buffer.pos(minX, maxY, maxZ).tex(minU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, maxZ).tex(maxU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, minZ).tex(maxU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, minZ).tex(minU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.SOUTH) {
            float minU = textureX + dimensionZ;
            float maxU = minU + dimensionX;
            float minV = textureY + dimensionZ;
            float maxV = minV + dimensionY;
            buffer.pos(minX, maxY, minZ).tex(minU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, minZ).tex(maxU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, minZ).tex(maxU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, minY, minZ).tex(minU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.NORTH) {
            float minU = textureX + dimensionX + dimensionZ + dimensionZ;
            float maxU = minU + dimensionX;
            float minV = textureY + dimensionZ;
            float maxV = minV + dimensionY;
            buffer.pos(minX, minY, maxZ).tex(maxU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, maxZ).tex(minU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, maxZ).tex(minU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, maxZ).tex(maxU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.WEST) {
            float minU = textureX;
            float maxU = minU + dimensionZ;
            float minV = textureY + dimensionZ;
            float maxV = minV + dimensionY;
            buffer.pos(maxX, maxY, minZ).tex(maxU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, maxZ).tex(minU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, maxZ).tex(minU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, minZ).tex(maxU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.EAST) {
            float minU = textureX + dimensionX + dimensionZ;
            float maxU = minU + dimensionZ;
            float minV = textureY + dimensionZ;
            float maxV = minV + dimensionY;
            buffer.pos(minX, minY, minZ).tex(minU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, minY, maxZ).tex(maxU / textureWidth, minV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, maxZ).tex(maxU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, minZ).tex(minU / textureWidth, maxV / textureHeight).normal(normalX, normalY, normalZ).endVertex();
        }
        tessellator.draw();
    }

    protected void drawBlockFace(EnumFacing facing, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float minU, float minV, float maxU, float maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        float normalX = facing.getFrontOffsetX();
        float normalY = facing.getFrontOffsetY();
        float normalZ = facing.getFrontOffsetZ();
        if (facing == EnumFacing.DOWN) {
            buffer.pos(minX, minY, minZ).tex(minU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, minZ).tex(maxU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, maxZ).tex(maxU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, minY, maxZ).tex(minU, maxV).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.UP) {
            buffer.pos(minX, maxY, maxZ).tex(minU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, maxZ).tex(maxU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, minZ).tex(maxU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, minZ).tex(minU, minV).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.SOUTH) {
            buffer.pos(minX, minY, maxZ).tex(minU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, maxZ).tex(maxU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, maxZ).tex(maxU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, maxZ).tex(minU, minV).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.NORTH) {
            buffer.pos(minX, maxY, minZ).tex(minU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, minZ).tex(maxU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, minZ).tex(maxU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, minY, minZ).tex(minU, maxV).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.WEST) {
            buffer.pos(minX, minY, minZ).tex(minU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, minY, maxZ).tex(maxU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, maxZ).tex(maxU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(minX, maxY, minZ).tex(minU, minV).normal(normalX, normalY, normalZ).endVertex();
        } else if (facing == EnumFacing.EAST) {
            buffer.pos(maxX, maxY, minZ).tex(minU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, maxY, maxZ).tex(maxU, minV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, maxZ).tex(maxU, maxV).normal(normalX, normalY, normalZ).endVertex();
            buffer.pos(maxX, minY, minZ).tex(minU, maxV).normal(normalX, normalY, normalZ).endVertex();
        }
        tessellator.draw();
    }

    protected void drawOutline(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        GlStateManager.disableLighting();

        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.glLineWidth(16.0F);
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(minX, minY, minZ).endVertex();
        buffer.pos(maxX, minY, minZ).endVertex();
        buffer.pos(maxX, minY, maxZ).endVertex();
        buffer.pos(minX, minY, maxZ).endVertex();
        buffer.pos(minX, minY, minZ).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(minX, maxY, minZ).endVertex();
        buffer.pos(maxX, maxY, minZ).endVertex();
        buffer.pos(maxX, maxY, maxZ).endVertex();
        buffer.pos(minX, maxY, maxZ).endVertex();
        buffer.pos(minX, maxY, minZ).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(minX, minY, minZ).endVertex();
        buffer.pos(minX, maxY, minZ).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(maxX, minY, minZ).endVertex();
        buffer.pos(maxX, maxY, minZ).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(minX, minY, maxZ).endVertex();
        buffer.pos(minX, maxY, maxZ).endVertex();
        tessellator.draw();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buffer.pos(maxX, minY, maxZ).endVertex();
        buffer.pos(maxX, maxY, maxZ).endVertex();
        tessellator.draw();
    }
}
