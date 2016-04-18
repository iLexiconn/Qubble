package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.QubbleModelBase;
import net.ilexiconn.qubble.client.model.QubbleModelRenderer;
import net.ilexiconn.qubble.server.model.qubble.QubbleModel;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.BufferUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;

@SideOnly(Side.CLIENT)
public class ModelViewComponent implements IComponent<QubbleGUI> {
    public ResourceLocation texture;
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

    private QubbleModelRenderer selected;
    private boolean dragged;

    private float partialTicks;

    @Override
    public void render(QubbleGUI gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        gui.drawOutline(0, 20, gui.width, gui.height - 20, QubbleGUI.getPrimaryColor(), 1);
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = scaledResolution.getScaleFactor();
        GL11.glScissor(0, 0, gui.width * scaleFactor, (gui.height - 21) * scaleFactor);
        if (gui.getCurrentModel() != null) {
            renderModel(gui, partialTicks, scaledResolution, false);
        }
        GlStateManager.enableTexture2D();
        this.prevMouseX = mouseX;
        this.prevMouseY = mouseY;
        this.prevCameraOffsetX = this.cameraOffsetX;
        this.prevCameraOffsetY = this.cameraOffsetY;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        int scroll = Mouse.getDWheel();
        this.zoomVelocity += (scroll / 120.0F) * 0.05F;
        this.zoom += this.zoomVelocity;
        this.zoomVelocity *= 0.6F;
        if (this.zoom < 0.5F) {
            this.zoom = 0.5F;
        } else if (this.zoom > 10.0F) {
            this.zoom = 10.0F;
        }
        this.partialTicks = partialTicks;
    }

    private void renderModel(QubbleGUI gui, float partialTicks, ScaledResolution scaledResolution, boolean selection) {
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
            int color = QubbleGUI.getSecondaryColor();
            float r = (float) (color >> 16 & 0xFF) / 255.0F;
            float g = (float) (color >> 8 & 0xFF) / 255.0F;
            float b = (float) (color & 0xFF) / 255.0F;
            GlStateManager.clearColor(r * 0.8F, g * 0.8F, b * 0.8F, 1.0F);
            GlStateManager.enableLighting();
            RenderHelper.enableGUIStandardItemLighting();
        }
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        this.setupCamera(10.0F, partialTicks);
        QubbleModel newModel = gui.getCurrentModel();
        if (this.currentModelContainer != newModel) {
            this.currentModel = new QubbleModelBase(newModel, false);
            this.currentModelSelection = new QubbleModelBase(newModel, true);
            this.currentModelContainer = newModel;
        }
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
        if (this.texture != null) {
            GlStateManager.enableTexture2D();
            ClientProxy.MINECRAFT.getTextureManager().bindTexture(this.texture);
        }
        if (!selection) {
            if (this.currentModel != null) {
                this.currentModel.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            }
        } else {
            if (this.currentModelSelection != null) {
                this.currentModelSelection.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            }
        }
        if (this.currentModel != null && !selection && this.selected != null) {
            this.currentModel.renderSelectedOutline(this.selected, 0.0625F);
        }
        GlStateManager.enableTexture2D();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, -5000.0D, 5000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
    }

    private void setupCamera(float scale, float partialTicks) {
        GlStateManager.disableTexture2D();
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0.0F, -2.0F, -10.0F);
        GlStateManager.scale(this.zoom, this.zoom, this.zoom);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.translate(QubbleGUI.interpolate(this.prevCameraOffsetX, this.cameraOffsetX, partialTicks), QubbleGUI.interpolate(this.prevCameraOffsetY, this.cameraOffsetY, partialTicks), 0.0F);
        GlStateManager.rotate(QubbleGUI.interpolate(this.prevRotationPitch, this.rotationPitch, partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(QubbleGUI.interpolate(this.prevRotationYaw, this.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void renderAfter(QubbleGUI gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {

    }

    @Override
    public boolean mouseClicked(QubbleGUI gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(QubbleGUI gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        float xMovement = mouseX - this.prevMouseX;
        float yMovement = mouseY - this.prevMouseY;
        if (button == 0) {
            this.rotationYaw += xMovement;
            if ((this.rotationPitch > -90.0F || yMovement < 0.0F) && (this.rotationPitch < 90.0F || yMovement > 0.0F)) {
                this.rotationPitch -= yMovement;
            }
            this.dragged = true;
            return true;
        } else if (button == 1) {
            this.cameraOffsetX = this.cameraOffsetX + xMovement * 0.016F;
            this.cameraOffsetY = this.cameraOffsetY + yMovement * 0.016F;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(QubbleGUI gui, float mouseX, float mouseY, int button) {
        if (button == 0) {
            if (!this.dragged && this.currentModel != null) {
                ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
                this.renderModel(gui, this.partialTicks, scaledResolution, true);
                FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
                GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
                int r = (int) (buffer.get(0) * 255.0F);
                int g = (int) (buffer.get(1) * 255.0F);
                int b = (int) (buffer.get(2) * 255.0F);
                int id = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
                if (this.selected != null) {
                    this.selected.setSelected(false);
                }
                this.selected = this.currentModel.getBox(id);
                if (this.selected != null) {
                    this.selected.setSelected(true);
                }
                return true;
            }
        }
        this.dragged = false;
        return false;
    }

    @Override
    public boolean keyPressed(QubbleGUI gui, char character, int key) {
        return false;
    }
}
