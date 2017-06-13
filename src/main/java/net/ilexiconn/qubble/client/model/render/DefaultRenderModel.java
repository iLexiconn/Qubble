package net.ilexiconn.qubble.client.model.render;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;

public class DefaultRenderModel extends QubbleRenderModel<DefaultCuboidWrapper, DefaultModelWrapper> {
    public DefaultRenderModel(DefaultModelWrapper model) {
        super(model);
    }

    @Override
    public void renderCuboid(DefaultCuboidWrapper cuboid, boolean selection) {
        GlStateManager.pushMatrix();
        this.postRender(cuboid);
        this.draw(cuboid, selection);
        for (DefaultCuboidWrapper child : cuboid.getChildren()) {
            this.renderCuboid(child, selection);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderCuboidSingle(DefaultCuboidWrapper cuboid, boolean selection) {
        GlStateManager.pushMatrix();
        this.postRender(cuboid);
        this.draw(cuboid, selection);
        GlStateManager.popMatrix();
    }

    private void draw(DefaultCuboidWrapper cuboid, boolean selection) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (selection) {
                int id = (this.getID(cuboid) << 3) | facing.getIndex() & 7;
                float selectionRed = (float) (id >> 16 & 0xFF) / 255.0F;
                float selectionGreen = (float) (id >> 8 & 0xFF) / 255.0F;
                float selectionBlue = (float) (id & 0xFF) / 255.0F;
                GlStateManager.color(selectionRed, selectionGreen, selectionBlue, 1.0F);
            }
            this.drawFace(cuboid, facing, this.model.getTextureWidth(), this.model.getTextureHeight());
        }
    }

    @Override
    public void postRender(DefaultCuboidWrapper cuboid) {
        GlStateManager.translate(cuboid.getPositionX() * 0.0625F, cuboid.getPositionY() * 0.0625F, cuboid.getPositionZ() * 0.0625F);
        if (cuboid.getRotationZ() != 0.0F) {
            GlStateManager.rotate(cuboid.getRotationZ(), 0.0F, 0.0F, 1.0F);
        }
        if (cuboid.getRotationY() != 0.0F) {
            GlStateManager.rotate(cuboid.getRotationY(), 0.0F, 1.0F, 0.0F);
        }
        if (cuboid.getRotationX() != 0.0F) {
            GlStateManager.rotate(cuboid.getRotationX(), 1.0F, 0.0F, 0.0F);
        }
        if (cuboid.getScaleX() != 1.0F || cuboid.getScaleY() != 1.0F || cuboid.getScaleZ() != 1.0F) {
            GlStateManager.scale(cuboid.getScaleX(), cuboid.getScaleY(), cuboid.getScaleZ());
        }
    }

    @Override
    public void renderOutline(DefaultCuboidWrapper cuboid, boolean hovering) {
        GlStateManager.pushMatrix();

        this.parentedPostRender(cuboid);
        int accent = LLibrary.CONFIG.getAccentColor();
        float red = (float) (accent >> 16 & 0xFF) / 255.0F;
        float green = (float) (accent >> 8 & 0xFF) / 255.0F;
        float blue = (float) (accent & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, hovering ? 0.5F : 1.0F);
        GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);

        float minX = cuboid.getOffsetX();
        float minY = cuboid.getOffsetY();
        float minZ = cuboid.getOffsetZ();
        float maxX = minX + cuboid.getDimensionX();
        float maxY = minY + cuboid.getDimensionY();
        float maxZ = minZ + cuboid.getDimensionZ();

        this.drawOutline(minX, minY, minZ, maxX, maxY, maxZ);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderSelection(DefaultCuboidWrapper cuboid, boolean hovering) {
        float scale = 0.0625F;
        GlStateManager.depthMask(false);
        this.renderOutline(cuboid, hovering);
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();

        GlStateManager.pushMatrix();
        DefaultCuboidWrapper parent = ModelHandler.INSTANCE.getParent(this.model, cuboid);
        if (parent != null) {
            this.parentedPostRender(parent);
        }
        if (this.model.getBaseTexture() != null) {
            GlStateManager.enableTexture2D();
            MC.getTextureManager().bindTexture(this.model.getBaseTexture().getLocation());
        }
        this.renderCuboidSingle(cuboid, false);
        if (this.model.getOverlayTexture() != null) {
            GlStateManager.enableTexture2D();
            MC.getTextureManager().bindTexture(this.model.getOverlayTexture().getLocation());
            this.renderCuboidSingle(cuboid, false);
        }
        GlStateManager.popMatrix();

        if (!hovering) {
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            int accent = LLibrary.CONFIG.getAccentColor();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            float red = (float) (accent >> 16 & 0xFF) / 255.0F;
            float green = (float) (accent >> 8 & 0xFF) / 255.0F;
            float blue = (float) (accent & 0xFF) / 255.0F;
            GlStateManager.color(red, green, blue, hovering ? 0.5F : 1.0F);
            this.parentedPostRender(cuboid);
            float scaleX = cuboid.getScaleX();
            float scaleY = cuboid.getScaleY();
            float scaleZ = cuboid.getScaleZ();
            GlStateManager.translate((-0.5F * scale) / scaleX, (-0.5F * scale) / scaleY, (-0.5F * scale) / scaleZ);
            GlStateManager.scale(0.15F, 0.15F, 0.15F);
            GlStateManager.translate((3.0F * scale) / scaleX, (-18.0F * scale) / scaleY, (3.0F * scale) / scaleZ);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F / scaleX, 1.0F / scaleY, 1.0F / scaleZ);
            ROTATION_POINT.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
            GlStateManager.popMatrix();
            GlStateManager.scale(0.8F, 0.8F, 0.8F);
            GlStateManager.translate(0.0F, 0.33F / scaleY, 0.0F);
            GlStateManager.color(red * 0.6F, green * 0.6F, blue * 0.6F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F / scaleX, 1.0F / scaleY, 1.0F / scaleZ);
            ROTATION_POINT.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
}
