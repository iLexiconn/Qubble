package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
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
    private BlockModelWrapper modelWrapper;
    private BlockCuboidWrapper cuboidWrapper;
    private float selectionRed, selectionGreen, selectionBlue;

    public QubbleVanillaModelRenderer(int id, BlockModelWrapper modelWrapper, BlockCuboidWrapper cuboidWrapper) {
        this.modelWrapper = modelWrapper;
        this.cuboidWrapper = cuboidWrapper;
        this.selectionRed = (float) (id >> 16 & 0xFF) / 255.0F;
        this.selectionGreen = (float) (id >> 8 & 0xFF) / 255.0F;
        this.selectionBlue = (float) (id & 0xFF) / 255.0F;
    }

    public void postRender(float scale) {
        this.translate(scale);
        QubbleVanillaCuboid cuboid = this.cuboidWrapper.getCuboid();
        GlStateManager.translate(cuboid.getFromX() * scale, cuboid.getFromY() * scale, cuboid.getFromZ() * scale);
    }

    public void render(float scale, boolean selection) {
        GlStateManager.pushMatrix();

        if (selection) {
            GlStateManager.color(this.selectionRed, this.selectionGreen, this.selectionBlue, 1.0F);
        }

        this.translate(scale);

        this.draw(scale, selection);
        GlStateManager.popMatrix();
    }

    public void renderSingle(float scale, boolean selection) {
        GlStateManager.pushMatrix();

        if (selection) {
            GlStateManager.color(this.selectionRed, this.selectionGreen, this.selectionBlue, 1.0F);
        }

        this.translate(scale);

        this.draw(scale, selection);

        GlStateManager.popMatrix();
    }

    public void draw(float scale, boolean selection) {
        if (this.cuboidWrapper.hasShade() && !selection) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
        QubbleVanillaCuboid cuboid = this.cuboidWrapper.getCuboid();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        float fromX = cuboid.getFromX() * scale;
        float fromY = cuboid.getFromY() * scale;
        float fromZ = cuboid.getFromZ() * scale;
        float toX = cuboid.getToX() * scale;
        float toY = cuboid.getToY() * scale;
        float toZ = cuboid.getToZ() * scale;
        for (EnumFacing facing : EnumFacing.VALUES) {
            QubbleVanillaFace face = cuboid.getFace(facing);
            if (face.isEnabled() || selection) {
                float minU = face.getMinU() / 16.0F;
                float minV = face.getMinV() / 16.0F;
                float maxU = face.getMaxU() / 16.0F;
                float maxV = face.getMaxV() / 16.0F;
                ModelTexture texture = this.modelWrapper.getTexture(face.getTexture());
                if (texture == null || selection) {
                    GlStateManager.disableTexture2D();
                } else {
                    GlStateManager.enableTexture2D();
                    ClientProxy.MINECRAFT.getTextureManager().bindTexture(texture.getLocation());
                }
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
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
                } else if (facing == EnumFacing.SOUTH) {
                    buffer.pos(fromX, fromY, toZ).tex(minU, maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
                    buffer.pos(toX, fromY, toZ).tex(maxU, maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
                    buffer.pos(toX, toY, toZ).tex(maxU, minV).normal(0.0F, 0.0F, 1.0F).endVertex();
                    buffer.pos(fromX, toY, toZ).tex(minU, minV).normal(0.0F, 0.0F, 1.0F).endVertex();
                } else if (facing == EnumFacing.NORTH) {
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
                tessellator.draw();
            }
        }
    }

    private void translate(float scale) {
        QubbleVanillaRotation rotation = this.cuboidWrapper.getCuboid().getRotation();

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

    public BlockCuboidWrapper getCuboidWrapper() {
        return this.cuboidWrapper;
    }
}
