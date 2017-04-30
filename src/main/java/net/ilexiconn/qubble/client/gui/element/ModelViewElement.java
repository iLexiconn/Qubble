package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.toolbar.ToolbarElement;
import net.ilexiconn.qubble.client.model.render.QubbleRenderModel;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.project.Project;
import net.ilexiconn.qubble.client.project.Selection;
import net.ilexiconn.qubble.client.project.action.CreateSideCuboid;
import net.ilexiconn.qubble.client.project.action.CuboidUpdateAction;
import net.ilexiconn.qubble.client.project.action.SetFaceTextureAction;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ModelViewElement extends Element<QubbleGUI> implements TextureDragAcceptor {
    private float cameraOffsetX = 0.0F;
    private float cameraOffsetY = 0.0F;
    private float rotationYaw = 225.0F;
    private float rotationPitch = -15.0F;
    private float prevRotationYaw = this.rotationYaw;
    private float prevRotationPitch = this.rotationPitch;
    private float prevCameraOffsetX;
    private float prevCameraOffsetY;

    private float zoom = 1.0F;
    private float zoomVelocity;

    private float prevMouseX;
    private float prevMouseY;

    private boolean dragging;
    private EnumFacing draggingFace;

    private float startMouseX;
    private float startMouseY;

    private boolean clicked;

    private float partialTicks;

    public ModelViewElement(QubbleGUI gui) {
        super(gui, 0.0F, 0.0F, gui.width, gui.height);
    }

    @Override
    public void init() {
        this.setWidth(this.gui.width);
        this.setHeight(this.gui.height);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        CuboidWrapper hovered = null;
        if (this.gui.getSelectedProject() != null) {
            boolean highlight = false;
            for (Element element : this.gui.getPostOrderElements()) {
                if (element instanceof ModelViewAdapter) {
                    ModelViewAdapter offset = (ModelViewAdapter) element;
                    if (offset.shouldHighlightHovered()) {
                        highlight = true;
                        break;
                    }
                }
            }
            if (highlight) {
                hovered = this.getSelection(this.gui.getSelectedProject().getModel()).getCuboid();
            }
        }
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = scaledResolution.getScaleFactor();
        GL11.glScissor(0, 0, this.gui.width * scaleFactor, (this.gui.height - this.gui.getToolbar().getHeight()) * scaleFactor);
        if (this.gui.getSelectedProject() != null) {
            this.renderModel(partialTicks, scaledResolution, false, hovered);
            if (!ClientProxy.MINECRAFT.gameSettings.hideGUI) {
                this.drawAxis(partialTicks, scaleFactor);
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.enableTexture2D();
        this.partialTicks = partialTicks;
        this.zoom += this.zoomVelocity;
        this.zoomVelocity *= 0.6F;
        if (this.zoom < 0.5F) {
            this.zoom = 0.5F;
        } else if (this.zoom > 10.0F) {
            this.zoom = 10.0F;
        }
    }

    private void drawAxis(float partialTicks, int scaleFactor) {
        GlStateManager.disableTexture2D();
        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        GlStateManager.translate(this.gui.getModelTree().getWidth(), this.gui.getToolbar().getHeight() + this.gui.getProjectBar().getHeight(), 0.0F);
        GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
        GlStateManager.translate(7.5F, 7.5F, -100.0F);
        float pitch = ClientUtils.interpolate(this.prevRotationPitch, this.rotationPitch, partialTicks);
        float yaw = ClientUtils.interpolate(this.prevRotationYaw, this.rotationYaw, partialTicks);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);

        double axisLength = 5.0;
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0, 0.0, 0.0).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(axisLength, 0.0, 0.0).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0, 0.0, 0.0).color(0.0F, 1.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0, axisLength, 0.0).color(0.0F, 1.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0, 0.0, 0.0).color(0.0F, 0.0F, 1.0F, 1.0F).endVertex();
        buffer.pos(0.0, 0.0, axisLength).color(0.0F, 0.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();

        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();

        this.renderAxisName("x", axisLength + 1.0, 0.0, 0.0, yaw, pitch, 0xFF0000);
        this.renderAxisName("y", 0.0, axisLength + 1.0, 0.0, yaw, pitch, 0x00FF00);
        this.renderAxisName("z", 0.0, 0.0, axisLength + 1.0, yaw, pitch, 0x0000FF);

        GlStateManager.popMatrix();
    }

    private void renderAxisName(String name, double x, double y, double z, float yaw, float pitch, int color) {
        FontRenderer fontRenderer = this.gui.getFontRenderer();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.2F, 0.2F, 0.2F);
        float offsetX = -fontRenderer.getStringWidth(name) / 2.0F;
        float offsetY = -fontRenderer.FONT_HEIGHT / 2.0F;
        GlStateManager.translate(offsetX, offsetY, offsetX);
        fontRenderer.drawString(name, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private void renderModel(float partialTicks, ScaledResolution scaledResolution, boolean selection, CuboidWrapper hovered) {
        GlStateManager.pushMatrix();
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableNormalize();
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GLU.gluPerspective(30.0F, (float) (scaledResolution.getScaledWidth_double() / scaledResolution.getScaledHeight_double()), 1.0F, 10000.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        if (selection) {
            GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            int color = LLibrary.CONFIG.getTertiaryColor();
            float r = (float) (color >> 16 & 0xFF) / 255.0F;
            float g = (float) (color >> 8 & 0xFF) / 255.0F;
            float b = (float) (color & 0xFF) / 255.0F;
            GlStateManager.clearColor(r, g, b, 1.0F);
            GlStateManager.enableLighting();
            RenderHelper.enableGUIStandardItemLighting();
        }
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        this.setupCamera(10.0F, partialTicks);
        Project project = this.gui.getSelectedProject();
        if (project != null) {
            ModelWrapper wrapper = project.getModel();
            GlStateManager.translate(0.0F, -1.5F, 0.0F);
            CuboidWrapper selectedCuboid = project.getSelectedCuboid();
            QubbleRenderModel renderModel = wrapper.getRenderModel();
            boolean hasSelection = selectedCuboid != null;
            if (!selection) {
                if (hasSelection || LLibrary.CONFIG.getColorMode().equals("light")) {
                    GlStateManager.color(0.7F, 0.7F, 0.7F, 1.0F);
                }
            }
            wrapper.render(selection);
            if (!selection) {
                if (Qubble.CONFIG.showGrid) {
                    GlStateManager.pushMatrix();
                    GlStateManager.disableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    Tessellator tessellator = Tessellator.getInstance();
                    VertexBuffer buffer = tessellator.getBuffer();
                    this.drawGrid(tessellator, buffer, 0.25F);
                    this.drawGrid(tessellator, buffer, 0.5F);
                    this.drawGrid(tessellator, buffer, 1.0F);
                    this.drawGrid(tessellator, buffer, 2.0F);
                    this.drawGrid(tessellator, buffer, 4.0F);
                    int accentColor = LLibrary.CONFIG.getAccentColor();
                    float red = (float) (accentColor >> 16 & 255) / 255.0F;
                    float green = (float) (accentColor >> 8 & 255) / 255.0F;
                    float blue = (float) (accentColor & 255) / 255.0F;
                    float gridY = 24.0F * 0.0625F;
                    GlStateManager.glLineWidth(4.0F);
                    buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                    buffer.pos(-0.5F, gridY, -0.5F).color(red, green, blue, 1.0F).endVertex();
                    buffer.pos(0.5F, gridY, -0.5F).color(red, green, blue, 1.0F).endVertex();
                    buffer.pos(0.5F, gridY, 0.5F).color(red, green, blue, 1.0F).endVertex();
                    buffer.pos(-0.5F, gridY, 0.5F).color(red, green, blue, 1.0F).endVertex();
                    tessellator.draw();
                    GlStateManager.depthMask(true);
                    GlStateManager.popMatrix();
                }
                if (hasSelection) {
                    GlStateManager.pushMatrix();
                    renderModel.renderSelection(selectedCuboid, false);
                    GlStateManager.popMatrix();
                }
                if (hovered != null) {
                    GlStateManager.pushMatrix();
                    wrapper.getRenderModel().renderSelection(hovered, true);
                    GlStateManager.popMatrix();
                }
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
            GlStateManager.disableBlend();
        }
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
    }

    private void drawGrid(Tessellator tessellator, VertexBuffer buffer, float size) {
        float scale = size / 4.0F;
        float lineWidth = Math.min(1.25F, 16.0F * (this.zoom / 4.0F) * (scale / 2.0F));
        GlStateManager.glLineWidth(lineWidth);
        float gridY = 24.0F * 0.0625F;
        size /= scale;
        int textColor = LLibrary.CONFIG.getTextColor();
        float red = (float) (textColor >> 16 & 255) / 255.0F;
        float green = (float) (textColor >> 8 & 255) / 255.0F;
        float blue = (float) (textColor & 255) / 255.0F;
        float alpha = Math.max(0.0F, Math.min(1.0F, lineWidth - 0.35F));
        for (float x = -size; x < size + scale; x += scale) {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(x, gridY, -size).color(red, green, blue, alpha).endVertex();
            buffer.pos(x, gridY, size).color(red, green, blue, alpha).endVertex();
            tessellator.draw();
        }
        for (float z = -size; z < size + scale; z += scale) {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-size, gridY, z).color(red, green, blue, alpha).endVertex();
            buffer.pos(size, gridY, z).color(red, green, blue, alpha).endVertex();
            tessellator.draw();
        }
    }

    private void setupCamera(float scale, float partialTicks) {
        ScaledResolution resolution = this.gui.getResolution();
        int offsetScale = resolution.getScaleFactor() * 3;
        List<ModelViewAdapter> adapters = GUIHelper.INSTANCE.elements(this.gui, ModelViewAdapter.class);
        for (ModelViewAdapter adapter : adapters) {
            GlStateManager.translate(adapter.getOffsetX() / offsetScale, adapter.getOffsetY() / offsetScale, adapter.getOffsetZ() / offsetScale);
        }
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
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        this.prevMouseX = mouseX;
        this.prevMouseY = mouseY;
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null && this.isSelected(mouseX, mouseY)) {
            this.clicked = true;
            ModelWrapper model = selectedProject.getModel();
            Selection<?, ?> selected = this.getSelection(model);
            CuboidWrapper selectedCuboid = selected.getCuboid();
            if (GuiScreen.isCtrlKeyDown()) {
                this.draggingFace = selected.getFacing();
            }
            if (GuiScreen.isAltKeyDown() && selectedCuboid != null) {
                this.gui.perform(new CreateSideCuboid(this.gui, selectedCuboid, selected.getFacing(), GuiScreen.isShiftKeyDown()));
                return true;
            } else {
                selectedProject.setSelectedCuboid(selectedCuboid);
                if (selectedCuboid != null) {
                    return true;
                }
            }
        }
        this.startMouseX = mouseX;
        this.startMouseY = mouseY;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (!this.gui.getModelTree().isParenting() && this.clicked) {
            if (!this.dragging) {
                this.updatePrevious();
                this.dragging = true;
            }
            float xMovement = mouseX - this.prevMouseX;
            float yMovement = mouseY - this.prevMouseY;
            this.prevMouseX = mouseX;
            this.prevMouseY = mouseY;
            if (this.draggingFace != null) {
                Project selectedProject = this.gui.getSelectedProject();
                if (selectedProject != null && selectedProject.getSelectedCuboid() != null) {
                    ModelWrapper model = selectedProject.getModel();
                    CuboidWrapper selectedCuboid = selectedProject.getSelectedCuboid();
                }
            } else {
                if (button == 0) {
                    this.rotationYaw += xMovement / this.zoom;
                    if ((this.rotationPitch > -90.0F || yMovement < 0.0F) && (this.rotationPitch < 90.0F || yMovement > 0.0F)) {
                        this.rotationPitch -= yMovement / this.zoom;
                    }
                    return true;
                } else if (button == 1) {
                    this.cameraOffsetX = this.cameraOffsetX + (xMovement / this.zoom) * 0.016F;
                    this.cameraOffsetY = this.cameraOffsetY + (yMovement / this.zoom) * 0.016F;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (!this.dragging) {
            this.updatePrevious();
        }
        this.dragging = false;
    }

    @Override
    public boolean mouseScrolled(float mouseX, float mouseY, int amount) {
        if (this.isSelected(mouseX, mouseY)) {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCuboid() != null && this.draggingFace != null) {
                CuboidWrapper selectedCuboid = selectedProject.getSelectedCuboid();
                EnumFacing.Axis axis = this.draggingFace.getAxis();
                EnumFacing.AxisDirection direction = this.draggingFace.getAxisDirection();
                float offsetX = 0.0F;
                float offsetY = 0.0F;
                float offsetZ = 0.0F;
                float offset = amount / 120.0F;
                if (axis == EnumFacing.Axis.X) {
                    offsetX = offset;
                } else if (axis == EnumFacing.Axis.Y) {
                    offsetY = offset;
                } else if (axis == EnumFacing.Axis.Z) {
                    offsetZ = offset;
                }
                CuboidWrapper updated = selectedCuboid.copyRaw();
                updated.setDimensions(selectedCuboid.getDimensionX() + offsetX, selectedCuboid.getDimensionY() + offsetY, selectedCuboid.getDimensionZ() + offsetZ);
                if (direction == (selectedCuboid.getModelType().isInverted() ? EnumFacing.AxisDirection.NEGATIVE : EnumFacing.AxisDirection.POSITIVE)) {
                    updated.setPosition(selectedCuboid.getPositionX() - offsetX, selectedCuboid.getPositionY() - offsetY, selectedCuboid.getPositionZ() - offsetZ);
                }
                this.gui.perform(new CuboidUpdateAction(this.gui, updated));
            } else {
                this.zoomVelocity += (amount / 120.0F) * 0.05F;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.dragging = false;
        this.draggingFace = null;
        this.clicked = false;
        return false;
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> Selection<CBE, MDL> getSelection(MDL model) {
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        this.renderModel(this.partialTicks, scaledResolution, true, null);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int red = (int) (buffer.get(0) * 255.0F);
        int green = (int) (buffer.get(1) * 255.0F);
        int blue = (int) (buffer.get(2) * 255.0F);
        int selectionID = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
        CBE cuboid = model.getRenderModel().getSelectedCuboid(selectionID >>> 3);
        if (cuboid != null) {
            int facingIndex = selectionID & 7;
            EnumFacing side = facingIndex >= 0 && facingIndex < EnumFacing.VALUES.length ? EnumFacing.VALUES[facingIndex] : EnumFacing.UP;
            return new Selection<>(model, cuboid, side);
        } else {
            return new Selection<>(model, null, EnumFacing.UP);
        }
    }

    @Override
    public boolean isSelected(float mouseX, float mouseY) {
        ToolbarElement toolbar = this.gui.getToolbar();
        ProjectBarElement projectBar = this.gui.getProjectBar();
        return this.gui.isElementOnTop(this) && mouseY >= toolbar.getPosY() + toolbar.getHeight() + (projectBar.isVisible() ? projectBar.getHeight() : 0);
    }

    private void updatePrevious() {
        this.prevCameraOffsetX = this.cameraOffsetX;
        this.prevCameraOffsetY = this.cameraOffsetY;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Override
    public boolean acceptTexture(String texture, float mouseX, float mouseY) {
        if (this.isSelected(mouseX, mouseY)) {
            Project selectedProject = this.gui.getSelectedProject();
            BlockModelWrapper model = selectedProject.getModel(ModelType.BLOCK);
            if (model != null) {
                Selection<BlockCuboidWrapper, BlockModelWrapper> selected = this.getSelection(model);
                BlockCuboidWrapper cuboid = selected.getCuboid();
                if (cuboid != null) {
                    EnumFacing[] sides = GuiScreen.isShiftKeyDown() ? new EnumFacing[] { selected.getFacing() } : EnumFacing.VALUES;
                    this.gui.perform(new SetFaceTextureAction(this.gui, cuboid.getName(), texture, sides));
                }
                selectedProject.setSelectedCuboid(cuboid);
                return true;
            }
        }
        return false;
    }
}
