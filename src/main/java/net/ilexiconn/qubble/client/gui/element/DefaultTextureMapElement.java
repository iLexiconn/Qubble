package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.sidebar.DefaultTextureSidebarHandler;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.project.Project;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class DefaultTextureMapElement extends Element<QubbleGUI> {
    private int dragOffsetX;
    private int dragOffsetY;

    public DefaultTextureMapElement(QubbleGUI gui, float posX, float posY, int width, int height) {
        super(gui, posX, posY, width, height);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null) {
            DefaultModelWrapper model = selectedProject.getModel(ModelType.DEFAULT);
            ModelTexture texture = model.getBaseTexture();
            GlStateManager.translate(this.getPosX(), this.getPosY(), 0.0F);
            String dimensions = model.getTextureWidth() + "x" + model.getTextureHeight();
            fontRenderer.drawString(dimensions, this.getWidth() - fontRenderer.getStringWidth(dimensions) - 1, this.getHeight() - fontRenderer.FONT_HEIGHT, LLibrary.CONFIG.getTextColor());
            float scale = this.getScale(model);
            GlStateManager.scale(scale, scale, 1.0F);
            if (texture != null) {
                ResourceLocation textureLocation = texture.getLocation();
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(textureLocation);
                GlStateManager.enableTexture2D();
                this.drawTexturedRectangle(0.0F, 0.0F, model.getTextureWidth(), model.getTextureHeight(), 0xFFFFFFFF);
            }
            boolean alpha = texture != null;
            for (DefaultCuboidWrapper cube : model.getCuboids()) {
                this.drawCube(cube, alpha);
            }
            DefaultCuboidWrapper selectedCuboid = selectedProject.getSelectedCuboid(ModelType.DEFAULT);
            if (selectedCuboid != null) {
                int textureX = selectedCuboid.getTextureX();
                int textureY = selectedCuboid.getTextureY();
                int dimensionX = (int) selectedCuboid.getDimensionX();
                int dimensionY = (int) selectedCuboid.getDimensionY();
                int dimensionZ = (int) selectedCuboid.getDimensionZ();
                int outlineColor = LLibrary.CONFIG.getAccentColor();
                this.fillRect(textureX, textureY + dimensionZ, dimensionZ, 0.5F, outlineColor);
                this.fillRect(textureX + dimensionX + dimensionZ + dimensionX, textureY + dimensionZ, dimensionZ, 0.5F, outlineColor);
                this.fillRect(textureX, textureY + dimensionZ + dimensionY - 0.5F, dimensionZ + dimensionX + dimensionZ + dimensionX, 0.5F, outlineColor);
                this.fillRect(textureX, textureY + dimensionZ, 0.5F, dimensionY, outlineColor);
                this.fillRect(textureX + dimensionX + dimensionZ + dimensionX + dimensionZ - 0.5F, textureY + dimensionZ, 0.5F, dimensionY, outlineColor);
                this.fillRect(textureX + dimensionZ, textureY, dimensionX + dimensionX, 0.5F, outlineColor);
                this.fillRect(textureX + dimensionZ, textureY + 0.5F, 0.5F, dimensionZ, outlineColor);
                this.fillRect(textureX + dimensionZ + dimensionX + dimensionX - 0.5F, textureY, 0.5F, dimensionZ + 0.5F, outlineColor);
            }
        } else {
            String text = "No Project Loaded";
            fontRenderer.drawString(text, this.getPosX() + (this.getWidth() / 2) - (fontRenderer.getStringWidth(text) / 2), this.getPosY() + (this.getHeight() / 2) - (fontRenderer.FONT_HEIGHT / 2), LLibrary.CONFIG.getTextColor(), false);
        }
        GlStateManager.popMatrix();
    }

    private float getScale(DefaultModelWrapper model) {
        int displayWidth = this.getWidth();
        int displayHeight = this.getHeight();
        float scale = 0.0F;
        while (displayWidth / (scale + 0.25F) >= model.getTextureWidth() && displayHeight / (scale + 0.25F) >= model.getTextureHeight()) {
            scale += 0.25F;
        }
        return scale;
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null) {
            if (this.isSelected(mouseX, mouseY)) {
                DefaultCuboidWrapper selectedCube = this.getSelectedCube(mouseX, mouseY, selectedProject);
                DefaultModelWrapper model = selectedProject.getModel(ModelType.DEFAULT);

                selectedProject.setSelectedCuboid(selectedCube);
                if (selectedCube != null) {
                    float scale = this.getScale(model);
                    this.dragOffsetX = (int) (selectedCube.getTextureX() - ((mouseX - this.getPosX()) / scale));
                    this.dragOffsetY = (int) (selectedCube.getTextureY() - ((mouseY - this.getPosY()) / scale));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null) {
            if (this.isSelected(mouseX, mouseY)) {
                DefaultCuboidWrapper selectedCube = selectedProject.getSelectedCuboid(ModelType.DEFAULT);
                if (selectedCube != null) {
                    DefaultModelWrapper model = selectedProject.getModel(ModelType.DEFAULT);
                    float scale = this.getScale(model);
                    int textureX = (int) ((mouseX - this.getPosX()) / scale) + this.dragOffsetX;
                    int textureY = (int) ((mouseY - this.getPosY()) / scale) + this.dragOffsetY;
                    SidebarHandler handler = this.gui.getSidebar().getHandler();
                    if (handler instanceof DefaultTextureSidebarHandler) {
                        DefaultTextureSidebarHandler textureHandler = (DefaultTextureSidebarHandler) handler;
                        textureHandler.propertyTextureX.setFloat(textureX);
                        textureHandler.propertyTextureY.setFloat(textureY);
                    } else {
                        this.gui.getSidebar().enable(model, selectedCube);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private DefaultCuboidWrapper getSelectedCube(float mouseX, float mouseY, Project selectedProject) {
        DefaultModelWrapper model = selectedProject.getModel(ModelType.DEFAULT);
        float scale = this.getScale(model);
        DefaultCuboidWrapper selected = null;
        for (DefaultCuboidWrapper cube : model.getCuboids()) {
            DefaultCuboidWrapper clicked = this.checkCubeClick(cube, mouseX, mouseY, scale);
            if (clicked != null) {
                selected = clicked;
                break;
            }
        }
        return selected;
    }

    private DefaultCuboidWrapper checkCubeClick(DefaultCuboidWrapper cube, float mouseX, float mouseY, float scale) {
        int textureX = cube.getTextureX();
        int textureY = cube.getTextureY();
        int dimensionX = (int) cube.getDimensionX();
        int dimensionY = (int) cube.getDimensionY();
        int dimensionZ = (int) cube.getDimensionZ();
        if (this.check(mouseX, mouseY, textureX, textureY + dimensionZ, dimensionX + dimensionZ + dimensionX + dimensionZ, dimensionY, scale) || this.check(mouseX, mouseY, textureX + dimensionZ, textureY, dimensionX + dimensionX, dimensionZ, scale)) {
            return cube;
        }
        for (DefaultCuboidWrapper child : cube.getChildren()) {
            DefaultCuboidWrapper clicked = this.checkCubeClick(child, mouseX, mouseY, scale);
            if (clicked != null) {
                return clicked;
            }
        }
        return null;
    }

    private void drawCube(DefaultCuboidWrapper cube, boolean alpha) {
        int textureX = cube.getTextureX();
        int textureY = cube.getTextureY();
        int dimensionX = (int) cube.getDimensionX();
        int dimensionY = (int) cube.getDimensionY();
        int dimensionZ = (int) cube.getDimensionZ();
        this.fillRect(textureX + dimensionZ, textureY, dimensionX, dimensionZ, alpha ? 0x2200FF00 : 0xFF00FF00);
        this.fillRect(textureX + dimensionX + dimensionZ, textureY, dimensionX, dimensionZ, alpha ? 0x2200AA00 : 0xFF00AA00);
        this.fillRect(textureX, textureY + dimensionZ, dimensionZ, dimensionY, alpha ? 0x22FF0000 : 0xFFFF0000);
        this.fillRect(textureX + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, alpha ? 0x220000FF : 0xFF0000FF);
        this.fillRect(textureX + dimensionX + dimensionZ, textureY + dimensionZ, dimensionZ, dimensionY, alpha ? 0x22AA0000 : 0xFFAA0000);
        this.fillRect(textureX + dimensionX + dimensionZ + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, alpha ? 0x220000AA : 0xFF0000AA);
        for (DefaultCuboidWrapper child : cube.getChildren()) {
            this.drawCube(child, alpha);
        }
    }

    private boolean check(float mouseX, float mouseY, int x, int y, int width, int height, float scale) {
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        x += this.getPosX();
        y += this.getPosY();
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void fillRect(float x, float y, float width, float height, int color) {
        this.drawRectangle(x, y, width, height, color);
    }
}
