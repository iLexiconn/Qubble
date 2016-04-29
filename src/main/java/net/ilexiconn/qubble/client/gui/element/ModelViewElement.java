package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.QubbleModelBase;
import net.ilexiconn.qubble.client.model.QubbleModelRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;

@SideOnly(Side.CLIENT)
public class ModelViewElement extends Element<QubbleGUI> {
    private float cameraOffsetX = 0.0F;
    private float cameraOffsetY = 0.0F;
    private float rotationYaw = 225.0F;
    private float rotationPitch = -15.0F;
    private float prevRotationYaw;
    private float prevRotationPitch;
    private float prevCameraOffsetX;
    private float prevCameraOffsetY;
    private float zoom = 1.0F;
    private float zoomVelocity;
    private QubbleModel currentModelContainer;
    private QubbleModelBase currentModel;
    private QubbleModelBase currentModelSelection;
    private float prevMouseX;
    private float prevMouseY;

    private boolean dragged;

    private float partialTicks;

    public ModelViewElement(QubbleGUI gui) {
        super(gui, 0.0F, 0.0F, gui.width, gui.height);
    }

    @Override
    public void init() {
        this.setWidth(getGUI().width);
        this.setHeight(getGUI().height);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        QubbleGUI gui = this.getGUI();
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = scaledResolution.getScaleFactor();
        GL11.glScissor(0, 0, gui.width * scaleFactor, (gui.height - gui.getToolbar().getHeight()) * scaleFactor);
        if (gui.getSelectedProject() != null) {
            this.renderModel(partialTicks, scaledResolution, false);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.enableTexture2D();
        this.prevMouseX = mouseX;
        this.prevMouseY = mouseY;
        int scroll = Mouse.getDWheel();
        this.zoomVelocity += (scroll / 120.0F) * 0.05F;
        this.zoom += this.zoomVelocity;
        this.prevCameraOffsetX = this.cameraOffsetX;
        this.prevCameraOffsetY = this.cameraOffsetY;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.zoomVelocity *= 0.6F;
        if (this.zoom < 0.5F) {
            this.zoom = 0.5F;
        } else if (this.zoom > 10.0F) {
            this.zoom = 10.0F;
        }
        this.partialTicks = partialTicks;
    }

    private void renderModel(float partialTicks, ScaledResolution scaledResolution, boolean selection) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableNormalize();
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GLU.gluPerspective(30.0F, (float) (scaledResolution.getScaledWidth_double() / scaledResolution.getScaledHeight_double()), 1.0F, 10000.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        if (selection) {
            GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            int color = Qubble.CONFIG.getTertiaryColor();
            float r = (float) (color >> 16 & 0xFF) / 255.0F;
            float g = (float) (color >> 8 & 0xFF) / 255.0F;
            float b = (float) (color & 0xFF) / 255.0F;
            GlStateManager.clearColor(r, g, b, 1.0F);
            GlStateManager.enableLighting();
            RenderHelper.enableGUIStandardItemLighting();
        }
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        this.setupCamera(10.0F, partialTicks);
        Project project = this.getGUI().getSelectedProject();
        QubbleModel newModel = project.getModel();
        this.currentModel = new QubbleModelBase(newModel, false);
        this.currentModelSelection = new QubbleModelBase(newModel, true);
        this.currentModelContainer = newModel;
        GlStateManager.translate(0.0F, -1.5F, 0.0F);
        QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
        QubbleModelRenderer selectedBox = this.currentModel.getCube(selectedCube);
        GlStateManager.enableBlend();
        if (selectedBox != null && !selection) {
            GlStateManager.color(0.7F, 0.7F, 0.7F, 1.0F);
        }
        if (!selection) {
            if (project.getBaseTexture() != null) {
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(project.getBaseTexture().getLocation());
            }
            this.currentModel.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            if (project.getOverlayTexture() != null) {
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(project.getOverlayTexture().getLocation());
                this.currentModel.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            }
        } else {
            this.currentModelSelection.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        }
        GlStateManager.disableBlend();
        if (selectedBox != null && !selection) {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            this.currentModel.renderSelectedOutline(selectedBox, 0.0625F);
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.pushMatrix();
            if (project.getBaseTexture() != null) {
                GlStateManager.enableTexture2D();
            }
            if (selectedBox.getParent() != null) {
                selectedBox.getParent().parentedPostRender(0.0625F);
            }
            if (project.getBaseTexture() != null) {
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(project.getBaseTexture().getLocation());
            }
            selectedBox.renderSingle(0.0625F);
            if (project.getOverlayTexture() != null) {
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(project.getOverlayTexture().getLocation());
                selectedBox.renderSingle(0.0625F);
            }
            GlStateManager.popMatrix();
        }

        if (!selection) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            this.drawGrid(tessellator, buffer, 0.25F, 0.0F);
            this.drawGrid(tessellator, buffer, 0.5F, 0.15F);
            this.drawGrid(tessellator, buffer, 1.0F, 0.225F);
            this.drawGrid(tessellator, buffer, 2.0F, 0.45F);
            this.drawGrid(tessellator, buffer, 4.0F, 0.9F);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, -5000.0D, 5000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
    }

    private void drawGrid(Tessellator tessellator, VertexBuffer buffer, float size, float color) {
        float scale = size / 4.0F;
        float lineWidth = 16.0F * (this.zoom / 4.0F) * (scale / 2.0F);
        GlStateManager.glLineWidth(lineWidth);
        float alpha = Math.max(0.0F, Math.min(1.0F, lineWidth - 0.35F));
        float gridY = 24.0F * 0.0625F;
        size /= scale;
        for (float x = -size; x < size + scale; x += scale) {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(x, gridY, -size).color(color, color, color, alpha).endVertex();
            buffer.pos(x, gridY, size).color(color, color, color, alpha).endVertex();
            tessellator.draw();
        }
        for (float z = -size; z < size + scale; z += scale) {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-size, gridY, z).color(color, color, color, alpha).endVertex();
            buffer.pos(size, gridY, z).color(color, color, color, alpha).endVertex();
            tessellator.draw();
        }
    }

    private void setupCamera(float scale, float partialTicks) {
        GlStateManager.disableTexture2D();
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0.0F, -2.0F, -10.0F);
        GlStateManager.scale(this.zoom, this.zoom, this.zoom);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.translate(ClientUtils.interpolate(this.prevCameraOffsetX, this.cameraOffsetX, partialTicks), ClientUtils.interpolate(this.prevCameraOffsetY, this.cameraOffsetY, partialTicks), 0.0F);
        GlStateManager.rotate(ClientUtils.interpolate(this.prevRotationPitch, this.rotationPitch, partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(ClientUtils.interpolate(this.prevRotationYaw, this.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.isSelected(mouseX, mouseY)) {
            float xMovement = mouseX - this.prevMouseX;
            float yMovement = mouseY - this.prevMouseY;
            if (button == 0) {
                this.rotationYaw += xMovement / this.zoom;
                if ((this.rotationPitch > -90.0F || yMovement < 0.0F) && (this.rotationPitch < 90.0F || yMovement > 0.0F)) {
                    this.rotationPitch -= yMovement / this.zoom;
                }
                this.dragged = true;
                return true;
            } else if (button == 1) {
                this.cameraOffsetX = this.cameraOffsetX + (xMovement / this.zoom) * 0.016F;
                this.cameraOffsetY = this.cameraOffsetY + (yMovement / this.zoom) * 0.016F;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        if (button == 0) {
            if (!this.dragged && this.getGUI().getSelectedProject() != null && this.currentModel != null && this.isSelected(mouseX, mouseY)) {
                ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
                this.renderModel(this.partialTicks, scaledResolution, true);
                FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
                GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
                int r = (int) (buffer.get(0) * 255.0F);
                int g = (int) (buffer.get(1) * 255.0F);
                int b = (int) (buffer.get(2) * 255.0F);
                int id = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
                QubbleCube cube = this.getGUI().getSelectedProject().getSelectedCube();
                if (cube != null) {
                    this.getGUI().getSelectedProject().setSelectedCube(null);
                }
                QubbleCube newCube = this.currentModel.getCube(id);
                this.getGUI().getSelectedProject().setSelectedCube(newCube);
                if (newCube != null) {
                    this.getGUI().getSelectedProject().setSelectedCube(newCube);
                    return true;
                }
                return false;
            }
        }
        this.dragged = false;
        return false;
    }

    @Override
    protected boolean isSelected(float mouseX, float mouseY) {
        ModelTreeElement modelTree = this.getGUI().getModelTree();
        ToolbarElement toolbar = this.getGUI().getToolbar();
        ProjectBarElement projectBar = this.getGUI().getProjectBar();
        return ElementHandler.INSTANCE.isOnTop(this.getGUI(), this, mouseX, mouseY) && mouseX > modelTree.getPosX() + modelTree.getWidth() && mouseY >= toolbar.getPosY() + toolbar.getHeight() + (projectBar.isVisible() ? projectBar.getHeight() : 0);
    }
}
