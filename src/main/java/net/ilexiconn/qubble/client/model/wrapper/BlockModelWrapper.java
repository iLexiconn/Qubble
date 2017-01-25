package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaModel;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.QubbleVanillaModelBase;
import net.ilexiconn.qubble.client.model.QubbleVanillaModelRenderer;
import net.ilexiconn.qubble.server.model.ModelHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class BlockModelWrapper implements ModelWrapper<BlockCuboidWrapper> {
    private QubbleVanillaModel model;
    private QubbleVanillaModelBase renderModel;
    private List<BlockCuboidWrapper> cuboids = new ArrayList<>();

    public BlockModelWrapper(QubbleVanillaModel model) {
        this.model = model;
        for (QubbleVanillaCuboid cuboid : model.getCuboids()) {
            this.cuboids.add(new BlockCuboidWrapper(cuboid));
        }
    }

    @Override
    public boolean reparent(BlockCuboidWrapper cuboid, BlockCuboidWrapper parent, boolean inPlace) {
        return false;
    }

    @Override
    public boolean supportsParenting() {
        return false;
    }

    @Override
    public void rebuildModel() {
        if (this.renderModel != null) {
            this.renderModel.delete();
        }
        this.renderModel = new QubbleVanillaModelBase(this);
    }

    @Override
    public void rebuildCuboid(BlockCuboidWrapper wrapper) {
        if (this.renderModel == null) {
            this.rebuildModel();
        } else {
            QubbleVanillaModelRenderer box = this.renderModel.getCuboid(wrapper);
            box.delete();
        }
    }

    @Override
    public void addCuboid(BlockCuboidWrapper cuboid) {
        this.cuboids.add(cuboid);
        this.model.addCuboid(cuboid.getCuboid());
        this.rebuildModel();
    }

    @Override
    public void copyCuboid(BlockCuboidWrapper cuboid) {
        this.addCuboid(new BlockCuboidWrapper(ModelHandler.INSTANCE.copy(this, cuboid)));
    }

    @Override
    public void deleteCuboid(BlockCuboidWrapper cuboid) {
        String name = cuboid.getName();
        this.cuboids.remove(cuboid);
        this.model.removeCuboid(name);
        this.rebuildModel();
    }

    @Override
    public void render(boolean clicking) {
        if (this.renderModel == null) {
            this.rebuildModel();
        }
        GlStateManager.pushMatrix();
        float scale = 0.0625F;
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.translate(-8.0F * scale, -24.0F * scale, -8.0F * scale);
        this.renderModel.render(scale, clicking);
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderSelection(BlockCuboidWrapper selectedCuboid, Project project) {
        QubbleVanillaModelRenderer renderCuboid = this.renderModel.getCuboid(selectedCuboid);
        if (renderCuboid != null) {
            float scale = 0.0625F;
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.scale(1.0F, -1.0F, 1.0F);
            GlStateManager.translate(-8.0F * scale, -24.0F * scale, -8.0F * scale);
            GlStateManager.depthMask(false);
            this.renderModel.renderSelectedOutline(renderCuboid, scale);
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.pushMatrix();
            if (project.getBaseTexture() != null) {
                GlStateManager.enableTexture2D();
                MC.getTextureManager().bindTexture(project.getBaseTexture().getLocation());
            }
            renderCuboid.renderSingle(scale, false);
            if (project.getOverlayTexture() != null) {
                GlStateManager.enableTexture2D();
                MC.getTextureManager().bindTexture(project.getOverlayTexture().getLocation());
                renderCuboid.renderSingle(scale, false);
            }
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            int accent = LLibrary.CONFIG.getAccentColor();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            float r = (float) (accent >> 16 & 0xFF) / 255.0F;
            float g = (float) (accent >> 8 & 0xFF) / 255.0F;
            float b = (float) (accent & 0xFF) / 255.0F;
            GlStateManager.color(r, g, b, 1.0F);
            float originX = (selectedCuboid.getOffsetX() - 0.5F) * scale;
            float originY = (selectedCuboid.getOffsetY() - 0.5F) * scale;
            float originZ = (selectedCuboid.getOffsetZ() - 0.5F) * scale;
            GlStateManager.translate(originX, originY, originZ);
            GlStateManager.scale(0.15F, 0.15F, 0.15F);
            GlStateManager.translate(3.0F * scale, -18.0F * scale, 3.0F * scale);
            ROTATION_POINT.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
            GlStateManager.scale(0.8F, 0.8F, 0.8F);
            GlStateManager.translate(0.0F, 0.33F, 0.0F);
            GlStateManager.color(r * 0.6F, g * 0.6F, b * 0.6F, 1.0F);
            ROTATION_POINT.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        }
    }

    @Override
    public BlockCuboidWrapper getSelected(int selectionID) {
        return this.renderModel.getCuboid(selectionID);
    }

    @Override
    public List<BlockCuboidWrapper> getCuboids() {
        return this.cuboids;
    }

    @Override
    public String getName() {
        return this.model.getName();
    }

    @Override
    public String getFileName() {
        return this.model.getFileName();
    }

    @Override
    public String getAuthor() {
        return this.model.getAuthor();
    }

    @Override
    public ModelWrapper<BlockCuboidWrapper> copy() {
        return new BlockModelWrapper(this.model.copy());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.model.serializeNBT();
    }

    @Override
    public BlockCuboidWrapper createCuboid(String name) {
        BlockCuboidWrapper cuboid = new BlockCuboidWrapper(QubbleVanillaCuboid.create(name, null, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
        this.addCuboid(cuboid);
        return cuboid;
    }

    @Override
    public ModelType getType() {
        return ModelType.JSON_VANILLA;
    }
}
