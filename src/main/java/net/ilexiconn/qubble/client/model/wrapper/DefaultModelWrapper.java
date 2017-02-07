package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.QubbleModelBase;
import net.ilexiconn.qubble.client.model.QubbleModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultModelWrapper extends ModelWrapper<DefaultCuboidWrapper> {
    private QubbleModel model;
    private QubbleModelBase renderModel;
    private List<DefaultCuboidWrapper> cuboids = new ArrayList<>();

    public DefaultModelWrapper(QubbleModel model) {
        this.model = model;
        for (QubbleCuboid cuboid : model.getCuboids()) {
            this.cuboids.add(new DefaultCuboidWrapper(this, cuboid));
        }
    }

    @Override
    public boolean reparent(DefaultCuboidWrapper cuboid, DefaultCuboidWrapper parent, boolean inPlace) {
        DefaultCuboidWrapper prevParent = this.getParent(cuboid);
        if (!this.hasChild(cuboid, parent)) {
            if (parent != cuboid) {
                if (inPlace) {
                    this.maintainParentTransformation(cuboid);
                    if (parent != null) {
                        this.inheritParentTransformation(cuboid, parent);
                    }
                }
                this.deleteCuboid(cuboid);
                if (parent != null && parent != prevParent) {
                    if (!parent.hasChild(cuboid)) {
                        parent.addChild(cuboid);
                    }
                } else if (parent == null) {
                    this.addCuboid(cuboid);
                }
                if (prevParent != null && parent != prevParent) {
                    prevParent.removeChild(cuboid);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supportsParenting() {
        return true;
    }

    @Override
    public void rebuildModel() {
        if (this.renderModel != null) {
            this.renderModel.delete();
        }
        this.renderModel = new QubbleModelBase(this);
    }

    @Override
    public void rebuildCuboid(DefaultCuboidWrapper wrapper) {
        if (this.renderModel == null) {
            this.rebuildModel();
        } else {
            QubbleCuboid cuboid = wrapper.getCuboid();
            QubbleModelRenderer box = this.renderModel.getCuboid(wrapper);
            box.delete();
            box.setRotationPoint(cuboid.getPositionX(), cuboid.getPositionY(), cuboid.getPositionZ());
            box.setTextureOffset(cuboid.getTextureX(), cuboid.getTextureY());
            box.cubeList.clear();
            box.addBox(cuboid.getOffsetX(), cuboid.getOffsetY(), cuboid.getOffsetZ(), cuboid.getDimensionX(), cuboid.getDimensionY(), cuboid.getDimensionZ(), 0.0F);
            box.rotateAngleX = (float) Math.toRadians(cuboid.getRotationX());
            box.rotateAngleY = (float) Math.toRadians(cuboid.getRotationY());
            box.rotateAngleZ = (float) Math.toRadians(cuboid.getRotationZ());
            box.mirror = cuboid.isTextureMirrored();
            box.scaleX = cuboid.getScaleX();
            box.scaleY = cuboid.getScaleY();
            box.scaleZ = cuboid.getScaleZ();
            box.compileDisplayList(0.0625F);
        }
    }

    @Override
    public void addCuboid(DefaultCuboidWrapper cuboid) {
        this.cuboids.add(cuboid);
        this.model.getCuboids().add(cuboid.getCuboid());
        this.rebuildModel();
    }

    @Override
    public boolean deleteCuboid(DefaultCuboidWrapper cuboid) {
        boolean removed = this.cuboids.remove(cuboid);
        this.model.getCuboids().remove(cuboid.getCuboid());
        this.rebuildModel();
        return removed;
    }

    @Override
    public void render(boolean clicking) {
        if (this.renderModel == null) {
            this.rebuildModel();
        }
        this.renderModel.render(0.0625F, clicking);
    }

    @Override
    public void renderSelection(DefaultCuboidWrapper selectedCuboid, Project project, boolean hovering) {
        QubbleModelRenderer renderCuboid = this.renderModel.getCuboid(selectedCuboid);
        if (renderCuboid != null) {
            float scale = 0.0625F;
            GlStateManager.depthMask(false);
            this.renderModel.renderSelectedOutline(renderCuboid, scale);
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.pushMatrix();
            if (renderCuboid.getParent() != null) {
                renderCuboid.getParent().parentedPostRender(scale);
            }
            if (this.getBaseTexture() != null) {
                GlStateManager.enableTexture2D();
                MC.getTextureManager().bindTexture(this.getBaseTexture().getLocation());
            }
            renderCuboid.renderSingle(scale, false);
            if (this.getOverlayTexture() != null) {
                GlStateManager.enableTexture2D();
                MC.getTextureManager().bindTexture(this.getOverlayTexture().getLocation());
                renderCuboid.renderSingle(scale, false);
            }
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
                GlStateManager.color(r, g, b, hovering ? 0.5F : 1.0F);
                renderCuboid.parentedPostRender(scale);
                float scaleX = renderCuboid.getParentedScaleX();
                float scaleY = renderCuboid.getParentedScaleY();
                float scaleZ = renderCuboid.getParentedScaleZ();
                GlStateManager.translate((-0.5F * scale) / scaleX, (-0.5F * scale) / scaleY, (0.5F * scale) / scaleZ);
                GlStateManager.scale(0.15F, 0.15F, 0.15F);
                GlStateManager.translate((3.0F * scale) / scaleX, (-18.0F * scale) / scaleY, (3.0F * scale) / scaleZ);
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.0F / scaleX, 1.0F / scaleY, 1.0F / scaleZ);
                ROTATION_POINT.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
                GlStateManager.popMatrix();
                GlStateManager.scale(0.8F, 0.8F, 0.8F);
                GlStateManager.translate(0.0F, 0.33F / scaleY, 0.0F);
                GlStateManager.color(r * 0.6F, g * 0.6F, b * 0.6F, 1.0F);
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

    @Override
    public DefaultCuboidWrapper getSelected(int selectionID) {
        return this.renderModel.getCuboid(selectionID);
    }

    @Override
    public List<DefaultCuboidWrapper> getCuboids() {
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
    public ModelWrapper<DefaultCuboidWrapper> copy() {
        return new DefaultModelWrapper(this.model.copy());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.model.serializeNBT();
    }

    @Override
    public DefaultCuboidWrapper createCuboid(String name) {
        QubbleCuboid cuboid = QubbleCuboid.create(name);
        cuboid.setDimensions(1, 1, 1);
        cuboid.setScale(1.0F, 1.0F, 1.0F);
        DefaultCuboidWrapper wrapper = new DefaultCuboidWrapper(this, cuboid);
        this.addCuboid(wrapper);
        return wrapper;
    }

    @Override
    public ModelType getType() {
        return ModelType.DEFAULT;
    }

    @Override
    public void importTextures(Map<String, ModelTexture> textures) {
        for (Map.Entry<String, String> entry : this.model.getTextures().entrySet()) {
            ModelTexture texture = textures.get(entry.getKey());
            if (texture != null) {
                texture.setName(entry.getValue());
                super.setTexture(entry.getKey(), texture);
            }
        }
    }

    @Override
    public void setTexture(String name, ModelTexture texture) {
        super.setTexture(name, texture);
        this.model.setTexture(name, texture != null ? texture.getName() : null);
    }

    public int getTextureWidth() {
        return this.model.getTextureWidth();
    }

    public int getTextureHeight() {
        return this.model.getTextureHeight();
    }

    public void setTextureWidth(int width) {
        this.model.setTextureWidth(width);
    }

    public void setTextureHeight(int height) {
        this.model.setTextureHeight(height);
    }

    public QubbleModel getModel() {
        return this.model;
    }

    public DefaultModelWrapper unparent() {
        return new DefaultModelWrapper(this.model.unparent());
    }
}
