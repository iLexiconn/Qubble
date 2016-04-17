package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.QubbleModelBase;
import net.ilexiconn.qubble.server.model.qubble.QubbleModel;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

@SideOnly(Side.CLIENT)
public class ModelViewComponent implements IComponent<QubbleGUI> {
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

    private float prevMouseX;
    private float prevMouseY;

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
            int color = QubbleGUI.getSecondaryColor();
            float r = (float) (color >> 16 & 0xFF) / 255.0F;
            float g = (float) (color >> 8 & 0xFF) / 255.0F;
            float b = (float) (color & 0xFF) / 255.0F;
            GlStateManager.clearColor(r * 0.8F, g * 0.8F, b * 0.8F, 1.0F);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
            GlStateManager.enableLighting();
            RenderHelper.enableGUIStandardItemLighting();
            this.setupCamera(10.0F, partialTicks);
            if (this.currentModelContainer != gui.getCurrentModel()) {
                this.currentModel = new QubbleModelBase(gui.getCurrentModel());
                this.currentModelContainer = gui.getCurrentModel();
            }
            GlStateManager.translate(0.0F, -1.0F, 0.0F);
            this.currentModel.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, -5000.0D, 5000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
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
        } else if (button == 1) {
            this.cameraOffsetX = this.cameraOffsetX + xMovement * 0.016F;
            this.cameraOffsetY = this.cameraOffsetY + yMovement * 0.016F;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(QubbleGUI gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(QubbleGUI gui, char character, int key) {
        return false;
    }
}
