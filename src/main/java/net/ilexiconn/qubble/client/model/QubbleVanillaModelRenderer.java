package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class QubbleVanillaModelRenderer {
    private QubbleVanillaCuboid cuboid;
    private int displayList;
    private boolean compiled;
    private float selectionRed, selectionGreen, selectionBlue;

    public QubbleVanillaModelRenderer(int id, QubbleVanillaCuboid cuboid) {
        this.cuboid = cuboid;
        this.selectionRed = (float) (id >> 16 & 0xFF) / 255.0F;
        this.selectionGreen = (float) (id >> 8 & 0xFF) / 255.0F;
        this.selectionBlue = (float) (id & 0xFF) / 255.0F;
    }

    public void postRender(float scale) {
        this.translate(scale);
        GlStateManager.translate(this.cuboid.getFromX() * scale, this.cuboid.getFromY() * scale, this.cuboid.getFromZ() * scale);
    }

    public void render(float scale, boolean selection) {
        GlStateManager.pushMatrix();

        if (selection) {
            GlStateManager.color(this.selectionRed, this.selectionGreen, this.selectionBlue, 1.0F);
        }

        if (!this.compiled) {
            this.compile(scale);
        }

        this.translate(scale);

        GlStateManager.callList(this.displayList);
        GlStateManager.popMatrix();
    }

    public void renderSingle(float scale, boolean selection) {
        GlStateManager.pushMatrix();

        if (selection) {
            GlStateManager.color(this.selectionRed, this.selectionGreen, this.selectionBlue, 1.0F);
        }

        if (!this.compiled) {
            this.compile(scale);
        }

        this.translate(scale);

        GlStateManager.callList(this.displayList);
        GlStateManager.popMatrix();
    }

    public void compile(float scale) {
        if (this.compiled) {
            GLAllocation.deleteDisplayLists(this.displayList);
            this.compiled = false;
        }
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(this.displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        float fromX = this.cuboid.getFromX() * scale;
        float fromY = this.cuboid.getFromY() * scale;
        float fromZ = this.cuboid.getFromZ() * scale;
        float toX = this.cuboid.getToX() * scale;
        float toY = this.cuboid.getToY() * scale;
        float toZ = this.cuboid.getToZ() * scale;
        for (EnumFacing facing : EnumFacing.VALUES) {
            QubbleVanillaFace face = this.cuboid.getFace(facing);
            if (face != null) {
                float minU = face.getMinU();
                float minV = face.getMinV();
                float maxU = face.getMaxU();
                float maxV = face.getMaxV();
                if (facing == EnumFacing.DOWN) {
                    buffer.pos(fromX, fromY, fromZ).tex(minU, minV).normal(0.0F, -1.0F, 0.0F).endVertex();
                    buffer.pos(toX, fromY, fromZ).tex(maxU, minV).normal(0.0F, -1.0F, 0.0F).endVertex();
                    buffer.pos(toX, fromY, toZ).tex(maxU, maxV).normal(0.0F, -1.0F, 0.0F).endVertex();
                    buffer.pos(fromX, fromY, toZ).tex(minU, maxV).normal(0.0F, -1.0F, 0.0F).endVertex();
                } else if (facing == EnumFacing.UP) {
                    buffer.pos(fromX, toY, toZ).tex(minU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(toX, toY, toZ).tex(maxU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(toX, toY, fromZ).tex(maxU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(fromX, toY, fromZ).tex(minU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
                } else if (facing == EnumFacing.NORTH) {
                    buffer.pos(fromX, fromY, toZ).tex(minU, maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
                    buffer.pos(toX, fromY, toZ).tex(maxU, maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
                    buffer.pos(toX, toY, toZ).tex(maxU, minV).normal(0.0F, 0.0F, 1.0F).endVertex();
                    buffer.pos(fromX, toY, toZ).tex(minU, minV).normal(0.0F, 0.0F, 1.0F).endVertex();
                } else if (facing == EnumFacing.SOUTH) {
                    buffer.pos(fromX, toY, fromZ).tex(minU, minV).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(toX, toY, fromZ).tex(maxU, minV).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(toX, fromY, fromZ).tex(maxU, maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(fromX, fromY, fromZ).tex(minU, maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
                } else if (facing == EnumFacing.WEST) {
                    buffer.pos(fromX, fromY, fromZ).tex(minU, maxV).normal(-1.0F, 0.0F, 0.0F).endVertex();
                    buffer.pos(fromX, fromY, toZ).tex(maxU, maxV).normal(-1.0F, 0.0F, 0.0F).endVertex();
                    buffer.pos(fromX, toY, toZ).tex(maxU, minV).normal(-1.0F, 0.0F, 0.0F).endVertex();
                    buffer.pos(fromX, toY, fromZ).tex(minU, minV).normal(-1.0F, 0.0F, 0.0F).endVertex();
                } else if (facing == EnumFacing.EAST) {
                    buffer.pos(toX, toY, fromZ).tex(minU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
                    buffer.pos(toX, toY, toZ).tex(maxU, minV).normal(1.0F, 0.0F, 0.0F).endVertex();
                    buffer.pos(toX, fromY, toZ).tex(maxU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
                    buffer.pos(toX, fromY, fromZ).tex(minU, maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
                }
            }
        }
        tessellator.draw();
        GlStateManager.glEndList();
        this.compiled = true;
    }

    public void delete() {
        if (this.compiled) {
            GLAllocation.deleteDisplayLists(this.displayList);
            this.compiled = false;
        }
    }

    private void translate(float scale) {
        QubbleVanillaRotation rotation = this.cuboid.getRotation();

        if (rotation != null) {
            GlStateManager.translate(rotation.getOriginX() * scale, rotation.getOriginY() * scale, rotation.getOriginZ() * scale);
        }

        if (rotation != null) {
            switch (rotation.getAxis()) {
                case X:
                    GlStateManager.rotate(rotation.getAngle(), 1.0F, 0.0F, 0.0F);
                    break;
                case Y:
                    GlStateManager.rotate(rotation.getAngle(), 0.0F, 1.0F, 0.0F);
                    break;
                case Z:
                    GlStateManager.rotate(rotation.getAngle(), 0.0F, 0.0F, 1.0F);
                    break;
            }
        }

        if (rotation != null) {
            GlStateManager.translate(-rotation.getOriginX() * scale, -rotation.getOriginY() * scale, -rotation.getOriginZ() * scale);
        }
    }

    public QubbleVanillaCuboid getCuboid() {
        return this.cuboid;
    }
}
