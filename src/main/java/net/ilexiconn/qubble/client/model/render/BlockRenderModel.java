package net.ilexiconn.qubble.client.model.render;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;

public class BlockRenderModel extends QubbleRenderModel<BlockCuboidWrapper, BlockModelWrapper> {
    public BlockRenderModel(BlockModelWrapper model) {
        super(model);
    }

    @Override
    public void renderCuboid(BlockCuboidWrapper cuboid, boolean selection) {
        GlStateManager.pushMatrix();
        this.postRender(cuboid);
        this.draw(cuboid, selection);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderCuboidSingle(BlockCuboidWrapper cuboid, boolean selection) {
        this.renderCuboid(cuboid, selection);
    }

    private void draw(BlockCuboidWrapper wrapper, boolean selection) {
        float scale = 0.0625F;
        if (wrapper.hasShade() && !selection) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
        QubbleVanillaCuboid cuboid = wrapper.getCuboid();
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
                ModelTexture texture = this.model.getTexture(face.getTexture());
                if (texture == null || selection) {
                    GlStateManager.disableTexture2D();
                } else {
                    GlStateManager.enableTexture2D();
                    ClientProxy.MINECRAFT.getTextureManager().bindTexture(texture.getLocation());
                }
                if (selection) {
                    int id = (this.getID(wrapper) << 3) | facing.getIndex() & 7;
                    float selectionRed = (float) (id >> 16 & 0xFF) / 255.0F;
                    float selectionGreen = (float) (id >> 8 & 0xFF) / 255.0F;
                    float selectionBlue = (float) (id & 0xFF) / 255.0F;
                    GlStateManager.color(selectionRed, selectionGreen, selectionBlue, 1.0F);
                }
                this.drawBlockFace(facing, fromX, fromY, fromZ, toX, toY, toZ, minU, minV, maxU, maxV);
            }
        }
    }

    @Override
    public void postRender(BlockCuboidWrapper cuboid) {
        float scale = 0.0625F;

        QubbleVanillaRotation rotation = cuboid.getCuboid().getRotation();

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

    @Override
    public void renderOutline(BlockCuboidWrapper wrapper, boolean hovering) {
        GlStateManager.pushMatrix();
        this.parentedPostRender(wrapper);
        int accent = LLibrary.CONFIG.getAccentColor();
        float red = (float) (accent >> 16 & 0xFF) / 255.0F;
        float green = (float) (accent >> 8 & 0xFF) / 255.0F;
        float blue = (float) (accent & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, hovering ? 0.5F : 1.0F);
        GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);

        QubbleVanillaCuboid cuboid = wrapper.getCuboid();
        this.drawOutline(cuboid.getFromX(), cuboid.getFromY(), cuboid.getFromZ(), cuboid.getToX(), cuboid.getToY(), cuboid.getToZ());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderSelection(BlockCuboidWrapper cuboid, boolean hovering) {
        float scale = 0.0625F;
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.translate(-8.0F * scale, -24.0F * scale, -8.0F * scale);
        GlStateManager.depthMask(false);
        this.renderOutline(cuboid, hovering);
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.pushMatrix();
        this.renderCuboidSingle(cuboid, false);
        GlStateManager.popMatrix();
        if (!hovering) {
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            int accent = LLibrary.CONFIG.getAccentColor();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            float r = (float) (accent >> 16 & 0xFF) / 255.0F;
            float g = (float) (accent >> 8 & 0xFF) / 255.0F;
            float b = (float) (accent & 0xFF) / 255.0F;
            GlStateManager.color(r, g, b, 1.0F);
            float originX = (cuboid.getOffsetX() - 0.5F) * scale;
            float originY = (cuboid.getOffsetY() - 0.5F) * scale;
            float originZ = (cuboid.getOffsetZ() - 0.5F) * scale;
            GlStateManager.translate(originX, originY, originZ);
            GlStateManager.scale(0.15F, 0.15F, 0.15F);
            GlStateManager.translate(3.0F * scale, -18.0F * scale, 3.0F * scale);
            ROTATION_POINT.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
            GlStateManager.scale(0.8F, 0.8F, 0.8F);
            GlStateManager.translate(0.0F, 0.33F, 0.0F);
            GlStateManager.color(r * 0.6F, g * 0.6F, b * 0.6F, 1.0F);
            ROTATION_POINT.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
    }
}
